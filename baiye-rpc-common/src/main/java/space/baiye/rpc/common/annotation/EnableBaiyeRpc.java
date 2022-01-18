package space.baiye.rpc.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Baiye on 2022/1/14.
 * 标识启用BaiyeRpc服务，指定扫描包
 *
 * @author Baiye
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) //保留到运行时仍然可见
public @interface EnableBaiyeRpc {

    String scanPackage() default "";

}
