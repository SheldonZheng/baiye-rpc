package space.baiye.rpc.server.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import space.baiye.rpc.common.annotation.RpcService;
import space.baiye.rpc.common.codec.MessageDecoder;
import space.baiye.rpc.common.codec.MessageEncoder;
import space.baiye.rpc.common.model.Config;
import space.baiye.rpc.common.model.RpcReq;

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

    private ServiceRegister serviceRegister;

    private String serverIP;

    private Integer serverPort;

    public void init() {
        InputStream inStream = ServiceStarter.class.getResourceAsStream("/baiyerpc.properties");
        this.properties = new Properties();
        try {
            this.properties.load(inStream);
        } catch (IOException e) {
            log.error("load properties error : {}",e);
            throw new RuntimeException("load properties error");
        }
        this.zookeeperAddress = this.properties.getProperty(Config.ZOOKEEPER_URL, "");
        this.serverIP = this.properties.getProperty(Config.SERVER_IP,"127.0.0.1");
        this.serverPort = Integer.valueOf(this.properties.getProperty(Config.SERVER_PORT,"8891"));
        this.serviceRegister = new ServiceRegister();
        log.info("properties loaded. zkAddress : {}, serverIP : {}, serverPort : {}",zookeeperAddress,serverIP,serverPort);
        scanBean();
        log.info("scan bean over. size : {}",serviceContainer.size());
        serviceRegister.connectZk(zookeeperAddress);
        log.info("connect to zk success. address : {}",zookeeperAddress);
        //register
        startNettyListen();


    }

    private void startNettyListen() {
        log.info("start netty listen...");

        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG,512)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("decoder",new MessageDecoder(RpcReq.class))
                                    .addLast("encoder",new MessageEncoder())
                                    .addLast("handler",new ServiceNettyHandler(serviceContainer));
                        }
                    });

            ChannelFuture fu = bootstrap.bind(serverPort).sync();
            log.info("bind port success . start listen");

            registerServiceToZk();

            fu.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("start netty error : {}",e);
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void registerServiceToZk() {
        String serverAddress = serverIP.concat(":").concat(serverPort.toString());
        log.info("register service to zk. address : {}",serverAddress);
        serviceContainer.forEach((k,v) -> {
            log.info("resgister service : {} ",k);
            serviceRegister.registerTempService(k,serverAddress);
        });
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
