package space.baiye.rpc.server.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import space.baiye.rpc.common.model.RpcReq;
import space.baiye.rpc.common.model.RpcRes;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by Baiye on 2022/1/19.
 * 实现Netty的入站处理器
 *
 * @author Baiye
 */
@Slf4j
public class ServiceNettyHandler extends ChannelInboundHandlerAdapter {

    private Map<String,Object> serviceContainer;

    public ServiceNettyHandler(Map<String,Object> serviceContainer) {
        super();
        this.serviceContainer = serviceContainer;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("enter channel read.");
        RpcReq req = (RpcReq) msg;
        log.info("receive req requestId : {}",req.getRequestId());

        RpcRes res = new RpcRes();
        try {
            Object result = invokeMethod(req);
            res.buildSuccessRes(result,req.getRequestId());
        } catch (Throwable throwable) {
            log.error("invoke method error requestId:{}, error : {}",req.getRequestId(),throwable);
            res.setErrorMsg(throwable.getMessage());
        }
        log.info("req process over requestId : {}",req.getRequestId());

        ctx.writeAndFlush(res).addListener((a) -> {
            log.debug("res write over");
        });

    }


    private Object invokeMethod(RpcReq req) throws Throwable {

        String interfaceName = req.getInterfaceName();
        log.info("invoke method interfaceName : {}",interfaceName);
        Object serviceClass = serviceContainer.get(interfaceName);

        String methodName = req.getMethodName();
        List<Pair<Class<?>, Object>> parameterList = req.getParameterList();
        Class<?>[] parameterTypes = new Class[parameterList.size()];
        Object[] params = new Object[parameterList.size()];

        for (int i = 0; i < parameterTypes.length; i++) {
            Pair<Class<?>, Object> pair = parameterList.get(i);
            parameterTypes[i] = pair.getLeft();
            params[i] = pair.getRight();
        }

        Class<?> cls = serviceClass.getClass();

        Method method = cls.getMethod(methodName,parameterTypes);

        Object res = method.invoke(serviceClass,params);

        log.info("invoke method over");

        return res;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("netty handler error:{}",cause);
    }
}
