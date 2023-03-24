package cn.iwenjuan.storage.utils;

/**
 * @author li1244
 * @date 2022/12/30 16:57
 */
public class ObjectUtils extends org.springframework.util.ObjectUtils {

    /**
     * 判断对象是否不为空
     *
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断是否有对象为空（全部对象为空才返回true）
     *
     * @param objects
     * @return
     */
    public static boolean isEmpty(Object... objects) {
        for (Object object : objects) {
            if (isNotEmpty(object)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否有对象不为空（全部对象都不为空才返回true）
     *
     * @param objects
     * @return
     */
    public static boolean isNotEmpty(Object... objects) {
        for (Object object : objects) {
            if (isEmpty(object)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断对象是否为null
     *
     * @param arg
     * @return
     */
    public static boolean isNull(Object arg) {
        return arg == null;
    }

    /**
     * 判断对象是否不为null
     *
     * @param arg
     * @return
     */
    public static boolean isNotNull(Object arg) {
        return !isNull(arg);
    }

    /**
     * 判断对象是否为null（全部为null才返回true）
     *
     * @param args
     * @return
     */
    public static boolean isNull(Object... args) {
        for (Object arg : args) {
            if (isNotNull(arg)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断对象是否不为null（全部不为null才返回true）
     *
     * @param args
     * @return
     */
    public static boolean isNotNull(Object... args) {
        for (Object arg : args) {
            if (isNull(arg)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取安全的字符串
     *
     * @param arg
     * @return
     */
    public static String getSafeString(String arg) {
        return getSafeString(arg, "");
    }

    /**
     * 获取安全的字符串
     *
     * @param arg
     * @param defaultValue
     * @return
     */
    public static String getSafeString(String arg, String defaultValue) {
        if (StringUtils.isNotBlank(arg)) {
            return arg;
        }
        return defaultValue;
    }

    /**
     * 获取安全的int
     *
     * @param arg
     * @return
     */
    public static int getSafeInt(Integer arg) {
        return getSafeInt(arg, 0);
    }

    /**
     * 获取安全的int
     *
     * @param arg
     * @param defaultValue
     * @return
     */
    public static int getSafeInt(Integer arg, int defaultValue) {
        if (arg != null) {
            return arg;
        }
        return defaultValue;
    }

    /**
     * 获取安全的long
     *
     * @param arg
     * @return
     */
    public static long getSafeLong(Long arg) {
        return getSafeLong(arg, 0L);
    }

    /**
     * 获取安全的long
     *
     * @param arg
     * @param defaultValue
     * @return
     */
    public static long getSafeLong(Long arg, long defaultValue) {
        if (arg != null) {
            return arg;
        }
        return defaultValue;
    }

}
