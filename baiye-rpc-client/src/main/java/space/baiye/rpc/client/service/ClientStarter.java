package space.baiye.rpc.client.service;

import lombok.extern.slf4j.Slf4j;
import space.baiye.rpc.common.annotation.UseRpc;
import space.baiye.rpc.common.model.Config;
import space.baiye.rpc.common.utils.ClassDetector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Baiye on 2022/2/4.
 * 客户端启动器
 *
 * @author Baiye
 */
@Slf4j
public class ClientStarter {
    private Properties properties;

    private String zookeeperAddress;

    private Map<String,Object> classContainer;

    private ServiceDiscovery serviceDiscovery;

    public void init() {
        InputStream inStream = ClientStarter.class.getResourceAsStream("/baiyerpc.properties");
        this.properties = new Properties();
        try {
            this.properties.load(inStream);
        } catch (IOException e) {
            log.error("load properties error : {}",e);
            throw new RuntimeException("load properties error");
        }
        this.serviceDiscovery = new ServiceDiscovery();
        this.zookeeperAddress = this.properties.getProperty(Config.ZOOKEEPER_URL, "");
        log.info("properties loaded. zkAddress : {}",zookeeperAddress);
        scanBean();
        log.info("scan bean over. size : {}", classContainer.size());
        serviceDiscovery.connectZk(zookeeperAddress);
        log.info("connect to zk success. address : {}",zookeeperAddress);
        //register

    }


    private void registerServiceToZk() {
      //  String serverAddress = serverIP.concat(":").concat(serverPort.toString());
    //    log.info("register service to zk. address : {}",serverAddress);
        classContainer.forEach((k, v) -> {
            log.info("resgister service : {} ",k);
        //   、、 serviceRegister.registerTempService(k,serverAddress);
        });
    }

    private void scanBean() {
        classContainer = new ConcurrentHashMap<>();
        ClassDetector classDetector = new ClassDetector();
        classDetector.init();
        Set<Class<?>> container = classDetector.scanClassWithAnnotation(UseRpc.class);
        for (Class<?> cls : container) {
            String clsName = cls.getName().substring(cls.getName().lastIndexOf(".") + 1);
            try {
                log.info("xxx : {}",clsName);
            } catch (Exception e) {
                log.error("init class : {} , error : {}", clsName,e);
                continue;
            }
        }

    }
}
