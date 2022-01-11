package space.baiye.rpc.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Baiye on 2022/1/11.
 *
 * 序列化方法
 *
 * @author Baiye
 */
@Slf4j
public class SerializationUtil {


    public static Object deserialize(byte[] data, Class<?> targetClass) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object o = ois.readObject();
            return o;
        } catch (IOException | ClassNotFoundException e) {
            log.error("{}",e);
            throw e;
        }
    }
}
