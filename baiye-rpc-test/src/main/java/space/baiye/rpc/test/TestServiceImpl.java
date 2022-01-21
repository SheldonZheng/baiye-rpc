package space.baiye.rpc.test;

import space.baiye.rpc.common.annotation.RpcService;

/**
 * Created by Baiye on 2022/1/18.
 *
 * @author Baiye
 */
@RpcService(serviceName = "TestService")
public class TestServiceImpl implements TestService {

    public String sayHello(String str) {
        return "hello".concat(str);
    }
}
