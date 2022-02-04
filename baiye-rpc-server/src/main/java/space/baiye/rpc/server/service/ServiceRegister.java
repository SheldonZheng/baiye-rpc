package space.baiye.rpc.server.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import space.baiye.rpc.common.model.Config;

/**
 * Created by Baiye on 2022/1/18.
 * 服务注册器，将扫描到的服务注册到zk
 *
 * @author Baiye
 */
@Slf4j
public class ServiceRegister {

    public static final String NODE = "node";

    private CuratorFramework client;

    public void connectZk(String zkAddress) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        this.client = CuratorFrameworkFactory.newClient(zkAddress,
                5000,1000,retryPolicy);
        this.client.start();
    }

    public void registerTempService(String serviceName,String localAddress) {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(Config.BASE_PATH.concat(serviceName).concat("/").concat(NODE),localAddress.getBytes());
        } catch (Exception e) {
            log.error("create temp path error: {}",e);
        }
    }
}
