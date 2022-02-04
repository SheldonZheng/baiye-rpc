package space.baiye.rpc.client.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Baiye on 2022/1/26.
 *
 * @author Baiye
 */
public class ServiceContainer {
    private static class ServiceContainerHolder {
        private static final ConcurrentHashMap<String, List<String>> MAP = new ConcurrentHashMap<String, List<String>>();
    }

    private ServiceContainer() {
    }

    public static final ConcurrentHashMap getInstance() {
        return ServiceContainerHolder.MAP;
    }
}
