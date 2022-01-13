package space.baiye.rpc.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import space.baiye.rpc.common.utils.SerializationUtils;

import java.util.List;

/**
 * Created by Baiye on 2022/1/11
 * .
 * 解码器 将网络传输的字节转换成实体类便于下一步处理
 * 直接继承了Netty的ByteToMessageDecoder
 * @author Baiye
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {

    private Class<?> targetClass;

    public MessageDecoder(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        byteBuf.markReaderIndex();

        int length = byteBuf.readInt();
        if (length < 0) {
            ctx.close();
        }
        if (byteBuf.readableBytes() < length) {
            log.info("reset read index !");
            byteBuf.resetReaderIndex();
        }


        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        Object obj = SerializationUtils.deserialize(data, targetClass);
        log.debug("deserialize over");

        list.add(obj);
    }
}
