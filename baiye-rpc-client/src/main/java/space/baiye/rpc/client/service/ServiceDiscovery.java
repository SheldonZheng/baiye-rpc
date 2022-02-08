package space.baiye.rpc.client.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Baiye on 2022/1/26.
 * 服务发现类 扫描已经注册的服务
 *
 * @author Baiye
 */
@Slf4j
public class ServiceDiscovery {

    private static String BASE_PATH = "/baiye/rpc/server/";

    private CuratorFramework client;

    public void connectZk(String zkAddress) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        this.client = CuratorFrameworkFactory.newClient(zkAddress,
                5000,1000,retryPolicy);
        this.client.start();
    }

    public List<String> getServiceList(String serviceName) throws Exception {
        final String upperPath = BASE_PATH.concat(serviceName);
        List<String> chidrenPaths = client.getChildren().forPath(upperPath);
        List<String> serverList = chidrenPaths.stream().map(subPath -> {
            try {
                return new String(client.getData().forPath(upperPath.concat(subPath)));
            } catch (Exception e) {
                log.error("get server data error : {}",e);
            }
            return null;
        }).collect(Collectors.toList());

        return serverList;
    }
}
