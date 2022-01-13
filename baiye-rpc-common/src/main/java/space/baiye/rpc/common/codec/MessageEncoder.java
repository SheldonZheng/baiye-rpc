package space.baiye.rpc.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import space.baiye.rpc.common.model.RpcRes;
import space.baiye.rpc.common.utils.SerializationUtils;

/**
 * Created by Baiye on 2022/1/12.
 *
 * 编码器 将实体类返回数据转为字节流
 *
 * @author Baiye
 */
@Slf4j
public class MessageEncoder extends MessageToByteEncoder<RpcRes> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRes rpcRes, ByteBuf byteBuf) throws Exception {
        log.info("encoder start");
        byte[] data = SerializationUtils.serialize(rpcRes);
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
