package space.baiye.rpc.server.service;

import lombok.extern.slf4j.Slf4j;
import space.baiye.rpc.common.annotation.RpcService;
import space.baiye.rpc.common.model.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Baiye on 2022/1/19.
 * 服务启动器
 *
 * @author Baiye
 */
@Slf4j
public class ServiceStarter {

    private Properties properties;

    private String zookeeperAddress;

    private ServiceDetector serviceDetector;

    private Map<String,Object> serviceContainer;

    public void init() {
        InputStream inStream = ServiceStarter.class.getResourceAsStream("/baiyerpc.properties");
        this.properties = new Properties();
        try {
            this.properties.load(inStream);
        } catch (IOException e) {
            log.error("load properties error : {}",e);
            throw new RuntimeException("load properties error");
        }
        zookeeperAddress = this.properties.getProperty(Config.ZOOKEEPER_URL, "");
        scanBean();
    }

    private void scanBean() {
        serviceContainer = new ConcurrentHashMap<>();
        serviceDetector = new ServiceDetector();
        serviceDetector.init();
        Set<Class<?>> container = serviceDetector.getContainer();
        for (Class<?> cls : container) {
            RpcService rpcService = cls.getAnnotation(RpcService.class);
            String serviceName = rpcService.serviceName();
            try {
                Object o = cls.newInstance();
                serviceContainer.put(serviceName,o);
            } catch (Exception e) {
                log.error("init class : {} , error : {}",cls.getName(),e);
                continue;
            }
        }

    }

}
