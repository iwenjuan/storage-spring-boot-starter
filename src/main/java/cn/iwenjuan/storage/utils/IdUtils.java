package cn.iwenjuan.storage.utils;

import java.util.UUID;

/**
 * @author li1244
 * @date 2022/12/30 16:24
 */
public class IdUtils {

    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
