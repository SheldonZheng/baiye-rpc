package space.baiye.rpc.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Baiye on 2022/2/4.
 * 表明这个接口是要使用RPC调用的
 *
 * @author Baiye
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) //保留到运行时仍然可见
public @interface UseRpc {
}
