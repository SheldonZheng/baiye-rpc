package space.baiye.rpc.client.service;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.tuple.Pair;
import space.baiye.rpc.common.model.RpcReq;
import space.baiye.rpc.common.model.RpcRes;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Baiye on 2022/2/8.
 *
 * @author Baiye
 */
@Slf4j
public class ProxyUtil {

    public static Object doProxyClass(Class<?> proxyClass, ServiceDiscovery serviceDiscovery) {
            log.info("do proxy for class : {}",proxyClass.getName());
            return Enhancer.create(proxyClass, new MethodInterceptor() {
                @Override
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    RpcReq req = new RpcReq();
                    req.setRequestId(UUID.randomUUID().toString());
                    String interfaceName = method.getDeclaringClass().getName();

                    List<String> serverList = serviceDiscovery.getServiceList(interfaceName);
                    if (serverList == null || serverList.size() == 0) {
                        log.error("server list is null : {}",interfaceName);
                        return null;
                    }
                    Random random = new Random();
                    String serverAddress = serverList.get(random.nextInt(serverList.size()));

                    String[] split = serverAddress.split(":");
                    RpcRequestSender sender = new RpcRequestSender(split[0],Integer.valueOf(split[1]));

                    req.setInterfaceName(interfaceName);
                    req.setMethodName(method.getName());
                    List<Pair<Class<?>,Object>> parameterList = new ArrayList<>();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    for (int i = 0; i < parameterTypes.length; i++) {
                        parameterList.add(Pair.of(parameterTypes[i],objects[i]));
                    }
                    req.setParameterList(parameterList);

                    RpcRes res = sender.sendReq(req);

                    log.info("invoke rpc over : {}",interfaceName);
                    return res.getResult();
                }
            });


    }
}
