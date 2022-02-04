package space.baiye.rpc.common.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import space.baiye.rpc.common.annotation.EnableBaiyeRpc;

import java.lang.annotation.Annotation;
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
@Getter
public class ClassDetector {

    private String scanPackage;

    public void init() {
        String callerClassName = getCallerClassName();
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
            this.scanPackage = scanPackage;
            return;
        } catch (ClassNotFoundException e) {
            log.error("ServiceDetector error:{}",e);
            throw new RuntimeException(e);
        }
    }

    public Set<Class<?>> scanClassWithAnnotation(Class<? extends Annotation> annotationClass) {
        log.info("scanPackage : {}", scanPackage);
        Set<Class<?>> classSet = ClassUtils.getClassSet(scanPackage);
        log.info("get classSet size : {}",classSet.size());
        Set<Class<?>> res = new HashSet<>();
        for (Class<?> cls : classSet) {
            if (cls.isAnnotationPresent(annotationClass)) {
                log.info("marked annotationClass class : {}",cls.getName());
                res.add(cls);
            }
        }
        log.info("scanPackage over, all marked class size : {}",res.size());
        return res;
    }

    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().startsWith("space.baiye.rpc.client") && !ste.getClassName().startsWith("space.baiye.rpc.server") && !ste.getClassName().equals(ClassDetector.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getClassName();
            }
        }
        return null;
    }


}
