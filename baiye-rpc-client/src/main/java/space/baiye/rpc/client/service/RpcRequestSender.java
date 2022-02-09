package space.baiye.rpc.client.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import space.baiye.rpc.common.codec.MessageDecoder;
import space.baiye.rpc.common.codec.MessageEncoder;
import space.baiye.rpc.common.model.RpcReq;
import space.baiye.rpc.common.model.RpcRes;

/**
 * Created by Baiye on 2022/2/8.
 * RPC 发起请求
 *
 * @author Baiye
 */
@Slf4j
public class RpcRequestSender extends SimpleChannelInboundHandler<RpcRes> {

    private String host;

    private Integer port;

    private RpcRes res;

    public RpcRequestSender(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public RpcRes sendReq(RpcReq req) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    // TCP 超时时间
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            // 解码器
                            sc.pipeline().addLast(new MessageDecoder(RpcRes.class));
                            // 编码器
                            sc.pipeline().addLast(new MessageEncoder());
                            //添加业务处理handler
                            sc.pipeline().addLast(RpcRequestSender.this);
                        }
                    });
            log.info("send request {},{}",host,port);
            ChannelFuture channelFuture = b.connect(host, port).sync();
            channelFuture.channel().writeAndFlush(req);

            log.info("wait server res...");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("send request error: {}",e);
        } finally {
            group.shutdownGracefully();
        }
        return this.res;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRes rpcRes) throws Exception {
        log.info("receiver res...");
        this.res = rpcRes;
        channelHandlerContext.close();
    }
}
