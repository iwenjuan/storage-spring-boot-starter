package cn.iwenjuan.storage.utils;

import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author li1244
 * @date 2022/12/1 16:02
 */
public class Md5Utils {

    /**
     * MD5加密
     *
     * @param message
     * @return
     */
    public static String md5(String message) {
        return DigestUtils.md5DigestAsHex(message.getBytes());
    }

    /**
     * MD5加密
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String md5(InputStream inputStream) throws IOException {
        return DigestUtils.md5DigestAsHex(inputStream);
    }

}
