package space.baiye.rpc.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * Created by Baiye on 2022/1/11.
 *
 * 序列化方法
 *
 * @author Baiye
 */
@Slf4j
public class SerializationUtils {


    public static Object deserialize(byte[] data, Class<?> targetClass) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object o = ois.readObject();
            return o;
        } catch (IOException | ClassNotFoundException e) {
            log.error("{}",e);
            throw e;
        }
    }

    public static byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(buffer)) {
            output.writeObject(o);
        }
        return buffer.toByteArray();
    }
}
