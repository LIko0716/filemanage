package com.hzvtc1063.filemanage.utils;

import com.hzvtc1063.filemanage.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;

/**
 * @author hangzhi1063
 * @date 2020/12/11 8:47
 */
public class SerializeUtil {




    public static byte[] serialize(Object object) {

        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        byte[] bytes = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            System.err.println("序列化失败" + e.getMessage());
        }
        return bytes;
    }

    // 反序列化
    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {

        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
        } catch (Exception e) {
            System.err.println("反序列化失败" + e.getMessage());
        }
        return ois.readObject();
    }



}
