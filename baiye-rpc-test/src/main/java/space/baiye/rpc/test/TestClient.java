package space.baiye.rpc.test;

import space.baiye.rpc.client.service.ClientStarter;
import space.baiye.rpc.common.annotation.EnableBaiyeRpc;

/**
 * Created by Baiye on 2022/2/4.
 *
 * @author Baiye
 */
@EnableBaiyeRpc(scanPackage = "space.baiye")
public class TestClient {

    public static void main(String args[]) {
        ClientStarter clientStarter = new ClientStarter();
        clientStarter.init();
    }
}
