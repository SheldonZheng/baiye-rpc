package space.baiye.rpc.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Baiye on 2022/1/12.
 * 标记在接口实现上的注解
 *
 * @author Baiye
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) //保留到运行时仍然可见
public @interface RpcService {

    //服务唯一标示，不能重复
    String serviceName() default "";

}
