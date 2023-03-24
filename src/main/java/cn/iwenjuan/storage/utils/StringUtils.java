package cn.iwenjuan.storage.utils;

import java.util.Collection;

/**
 * @author li1244
 * @date 2023/2/16 17:54
 */
public class StringUtils extends org.springframework.util.StringUtils {

    /**
     * 判断字符串是否为null
     *
     * @param arg
     * @return
     */
    public static boolean isNull(CharSequence arg) {
        return arg == null;
    }

    /**
     * 判断字符串是否不为null
     *
     * @param arg
     * @return
     */
    public static boolean isNotNull(CharSequence arg) {
        return !isNull(arg);
    }

    /**
     * 判断字符串是否为null（全部为null才返回true）
     *
     * @param args
     * @return
     */
    public static boolean isNull(CharSequence... args) {
        for (CharSequence arg : args) {
            if (isNotNull(arg)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为null（全部不为null才返回true）
     *
     * @param args
     * @return
     */
    public static boolean isNotNull(CharSequence... args) {
        for (CharSequence arg : args) {
            if (isNull(arg)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否为空
     *
     * @param arg
     * @return
     */
    public static boolean isEmpty(CharSequence arg) {
        return arg == null || arg.length() == 0;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param arg
     * @return
     */
    public static boolean isNotEmpty(CharSequence arg) {
        return !isEmpty(arg);
    }

    /**
     * 判断字符串是否为空，都为空才返回true
     *
     * @param args
     * @return
     */
    public static boolean isEmpty(CharSequence... args) {
        for (CharSequence arg : args) {
            if (isNotEmpty(arg)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为空，都不为空才返回true
     *
     * @param args
     * @return
     */
    public static boolean isNotEmpty(CharSequence... args) {
        for (CharSequence arg : args) {
            if (isEmpty(arg)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否为空（去除前后空格）
     *
     * @param arg
     * @return
     */
    public static boolean isBlank(String arg) {
        return arg == null || arg.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空（去除前后空格）
     *
     * @param arg
     * @return
     */
    public static boolean isNotBlank(String arg) {
        return !isBlank(arg);
    }

    /**
     * 判断字符串是否为空，都为空才返回true（去除前后空格）
     *
     * @param args
     * @return
     */
    public static boolean isBlank(String... args) {
        for (String arg : args) {
            if (isNotBlank(arg)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为空，都不为空才返回true（去除前后空格）
     *
     * @param args
     * @return
     */
    public static boolean isNotBlank(String... args) {
        for (String arg : args) {
            if (isBlank(arg)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串拼接
     *
     * @param collection
     * @param separator
     * @return
     */
    public static String join(Collection<String> collection, String separator) {
        StringBuilder builder = new StringBuilder();
        for (String item : collection) {
            builder.append(item).append(separator);
        }
        String str = builder.toString();
        if (isBlank(str)) {
            return str;
        }
        return str.substring(0, str.lastIndexOf(separator));
    }

    /**
     * 字符串拼接
     *
     * @param
     * @param separator
     * @return
     */
    public static String join(String[] array, String separator) {
        StringBuilder builder = new StringBuilder();
        for (String item : array) {
            builder.append(item).append(separator);
        }
        String str = builder.toString();
        if (isBlank(str)) {
            return str;
        }
        return str.substring(0, str.lastIndexOf(separator));
    }

    /**
     * 首字母转大写
     *
     * @param arg
     * @return
     */
    public static String firstCharacterToUpperCase(String arg) {
        return changeFirstCharacterCase(arg, true);
    }

    /**
     * 首字母转大写
     *
     * @param arg
     * @return
     */
    public static String firstCharacterToLowerCase(String arg) {
        return changeFirstCharacterCase(arg, false);
    }

    /**
     * 修改首字母大小写
     *
     * @param arg
     * @param capitalize
     * @return
     */
    private static String changeFirstCharacterCase(String arg, boolean capitalize) {
        if (isBlank(arg)) {
            return arg;
        } else {
            char baseChar = arg.charAt(0);
            char updatedChar;
            if (capitalize) {
                updatedChar = Character.toUpperCase(baseChar);
            } else {
                updatedChar = Character.toLowerCase(baseChar);
            }
            if (baseChar == updatedChar) {
                return arg;
            } else {
                char[] chars = arg.toCharArray();
                chars[0] = updatedChar;
                return new String(chars);
            }
        }
    }
}
