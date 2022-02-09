package space.baiye.rpc.test;

import space.baiye.rpc.common.annotation.EnableBaiyeRpc;
import space.baiye.rpc.server.ServiceStarter;

/**
 * Created by Baiye on 2022/1/14.
 *
 * @author Baiye
 */
@EnableBaiyeRpc(scanPackage = "space.baiye")
public class TestServer {

    public static void main(String args[]) {
        ServiceStarter serviceStarter = new ServiceStarter();
        serviceStarter.init();

    }

}
