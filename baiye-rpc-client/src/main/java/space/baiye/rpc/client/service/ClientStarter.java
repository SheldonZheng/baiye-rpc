package space.baiye.rpc.client.service;

import lombok.extern.slf4j.Slf4j;
import space.baiye.rpc.common.annotation.UseRpc;
import space.baiye.rpc.common.model.Config;
import space.baiye.rpc.common.utils.ClassDetector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

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

    private ServiceDiscovery serviceDiscovery;


    public ServiceDiscovery init() {
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
        Set<Class<?>> proxyClasses = scanBean();
        log.info("scan bean over. size : {}", proxyClasses.size());
        serviceDiscovery.connectZk(zookeeperAddress);
        log.info("connect to zk success. address : {}",zookeeperAddress);
        return this.serviceDiscovery;
    }




    private Set<Class<?>> scanBean() {
        ClassDetector classDetector = new ClassDetector();
        classDetector.init();
        Set<Class<?>> container = classDetector.scanClassWithAnnotation(UseRpc.class);

        return container;
    }
}
