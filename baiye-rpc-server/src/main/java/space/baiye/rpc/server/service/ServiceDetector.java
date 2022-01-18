package space.baiye.rpc.server.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import space.baiye.rpc.common.annotation.EnableBaiyeRpc;
import space.baiye.rpc.common.annotation.RpcService;
import space.baiye.rpc.common.utils.ClassUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Baiye on 2022/1/13.
 *
 * 服务发现，扫描，注册
 *
 * @author Baiye
 */
@Slf4j
public class ServiceDetector {

    private Set<Class<?>> container;

    public void init() {
        container = new HashSet<Class<?>>();
        String callerClassName = getCallerClassName();
        System.out.println(callerClassName);
        try {
            Class<?> callerClass = Class.forName(callerClassName,false,Thread.currentThread().getContextClassLoader());
            EnableBaiyeRpc annotation = callerClass.getAnnotation(EnableBaiyeRpc.class);
            if (annotation == null) {
                throw new RuntimeException("not mark EnableBaiyeRpc annotation on start class.");
            }
            String scanPackage = annotation.scanPackage();
            if (StringUtils.isEmpty(scanPackage)) {
                log.info("scanPackage is empty , use caller class package.");
                scanPackage = callerClassName.substring(0,callerClassName.lastIndexOf("."));
            }
            log.info("scanPackage : {}",scanPackage);
            Set<Class<?>> classSet = ClassUtils.getClassSet(scanPackage);
            log.info("get classSet size : {}",classSet.size());
            for (Class<?> cls : classSet) {
                if (cls.isAnnotationPresent(RpcService.class)) {
                    log.info("marked RpcService class : {}",cls.getName());
                    container.add(cls);
                }
            }
            log.info("scanPackage over, all marked class size : {}",container.size());
            return;
        } catch (ClassNotFoundException e) {
            log.error("ServiceDetector error:{}",e);
            throw new RuntimeException(e);
        }
    }

    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().startsWith("space.baiye.rpc.server") && !ste.getClassName().equals(ServiceDetector.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getClassName();
            }
        }
        return null;
    }
}
