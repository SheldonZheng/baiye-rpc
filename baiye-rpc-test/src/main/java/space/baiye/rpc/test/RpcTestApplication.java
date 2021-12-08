package space.baiye.rpc.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by Baiye on 2021/12/8.
 *
 * @author Baiye
 */
@SpringBootApplication(scanBasePackages = "space.baiye.rpc")
public class RpcTestApplication {
    public static void main(String args[]) {
        SpringApplication.run(RpcTestApplication.class,args);
    }
}
