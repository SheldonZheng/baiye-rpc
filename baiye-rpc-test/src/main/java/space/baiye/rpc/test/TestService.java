package space.baiye.rpc.test;

import space.baiye.rpc.common.annotation.UseRpc;

/**
 * Created by Baiye on 2022/1/18.
 *
 * @author Baiye
 */
@UseRpc
public interface TestService {

    public String sayHello(String str);
}
