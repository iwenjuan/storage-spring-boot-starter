package cn.iwenjuan.storage.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author lid
 * @date 2023/2/8 15:21
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * 匹配getter方法的正则表达式
     */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /**
     * 匹配setter方法的正则表达式
     */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * 对象属性复制
     *
     * @param source
     * @param target
     * @param ignoreProperties
     */
    public static void copy(Object source, Object target, String... ignoreProperties) {
        if (source == null) {
            return;
        }
        List<String> ignoreList = null;
        if (ignoreProperties != null) {
            ignoreList = Arrays.asList(ignoreProperties);
        }
        Class<?> sourceClass = source.getClass();
        Map<String, Method> getterMethods = getGetterMethods(sourceClass);
        Class<?> targetClass = target.getClass();
        Map<String, Method> setterMethods = getSetterMethods(targetClass);
        for (Map.Entry<String, Method> entry : setterMethods.entrySet()) {
            Method setterMethod = entry.getValue();
            String setterMethodName = setterMethod.getName();
            String propertyName = setterMethodName.substring(3);
            if (ignoreList != null && ignoreList.contains(StringUtils.firstCharacterToLowerCase(propertyName))) {
                continue;
            }
            String getterMethodName = "get".concat(propertyName);
            if (getterMethods.containsKey(getterMethodName)) {
                Method getterMethod = getterMethods.get(getterMethodName);
                try {
                    Object invoke = getterMethod.invoke(source);
                    if (invoke == null) {
                        continue;
                    }
                    Class<?> parameterType = setterMethod.getParameterTypes()[0];
                    setterMethod.invoke(target, convert(invoke, parameterType));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 值转换
     *
     * @param invoke
     * @param parameterType
     * @return
     */
    private static Object convert(Object invoke, Class<?> parameterType) {
        Object result = invoke;
        if (parameterType == Long.class || parameterType == long.class) {
            result = Long.valueOf(String.valueOf(invoke));
        }
        if (parameterType == Integer.class || parameterType == int.class) {
            result = Integer.valueOf(String.valueOf(invoke));
        }
        if (parameterType == Double.class || parameterType == double.class) {
            result = Double.valueOf(String.valueOf(invoke));
        }
        if (parameterType == Float.class || parameterType == float.class) {
            result = Float.valueOf(String.valueOf(invoke));
        }
        return result;
    }

    /**
     * 获取对象的setter方法。
     *
     * @param obj 对象
     * @return 对象的setter方法列表
     */
    public static Map<String, Method> getSetterMethods(Object obj) {
        return getSetterMethods(obj.getClass());
    }

    /**
     * 获取setter方法。
     *
     * @param clazz
     * @return
     */
    public static Map<String, Method> getSetterMethods(Class<?> clazz) {
        // setter方法列表
        Map<String, Method> setterMethods = new HashMap<>(16);
        // 获取所有方法
        Method[] methods = clazz.getMethods();
        // 查找setter方法
        for (Method method : methods) {
            Matcher m = SET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 1)) {
                setterMethods.put(method.getName(), method);
            }
        }
        return setterMethods;
    }

    /**
     * 获取getter方法
     *
     * @param obj
     * @return
     */
    public static Map<String, Method> getGetterMethods(Object obj) {
        return getGetterMethods(obj.getClass());
    }

    /**
     * 获取getter方法
     *
     * @param clazz
     * @return
     */
    public static Map<String, Method> getGetterMethods(Class<?> clazz) {
        // getter方法列表
        Map<String, Method> getterMethods = new HashMap<>(16);
        // 获取所有方法
        Method[] methods = clazz.getMethods();
        // 查找getter方法
        for (Method method : methods) {
            Matcher m = GET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 0)) {
                getterMethods.put(method.getName(), method);
            }
        }
        return getterMethods;
    }

    /**
     * 执行set方法
     *
     * @param object
     * @param setterMethodName
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void invokeSetterMethod(Object object, String setterMethodName, Object value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        invokeSetterMethod(object, getSetterMethods(object), setterMethodName, value);
    }

    /**
     * 执行set方法
     *
     * @param object
     * @param setterMethods
     * @param setterMethodName
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void invokeSetterMethod(Object object, Map<String, Method> setterMethods, String setterMethodName, Object value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (setterMethods.containsKey(setterMethodName)) {
            invokeSetterMethod(object, setterMethods.get(setterMethodName), value);
            return;
        }
        throw new NoSuchMethodException();
    }

    /**
     * 执行set方法
     *
     * @param object
     * @param setterMethod
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void invokeSetterMethod(Object object, Method setterMethod, Object value) throws InvocationTargetException, IllegalAccessException {
        invokeMethod(object, setterMethod, value);
    }

    /**
     * 执行get方法
     *
     * @param object
     * @param getterMethodName
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeGetterMethod(Object object, String getterMethodName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invokeGetterMethod(object, getGetterMethods(object), getterMethodName);
    }

    /**
     * 执行get方法
     *
     * @param object
     * @param getterMethods
     * @param getterMethodName
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeGetterMethod(Object object, Map<String, Method> getterMethods, String getterMethodName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (getterMethods.containsKey(getterMethodName)) {
            return invokeGetterMethod(object, getterMethods.get(getterMethodName));
        }
        throw new NoSuchMethodException();
    }

    /**
     * 执行get方法
     *
     * @param object
     * @param getterMethod
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeGetterMethod(Object object, Method getterMethod) throws InvocationTargetException, IllegalAccessException {
        return invokeMethod(object, getterMethod);
    }

    /**
     * 执行方法
     *
     * @param object
     * @param method
     * @param args
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(object, args);
    }

    /**
     * 根据名称获取方法
     *
     * @param object
     * @param methodName
     * @return
     */
    public static List<Method> getMethods(Object object, String methodName) {
        return getMethods(object.getClass(), methodName);
    }

    /**
     * 根据名称获取方法
     *
     * @param beanClass
     * @param methodName
     * @return
     */
    public static List<Method> getMethods(Class<?> beanClass, String methodName) {
        List<Method> methods = new ArrayList<>();
        Method[] beanMethods = beanClass.getMethods();
        for (Method method : beanMethods) {
            if (method.getName().equals(methodName)) {
                methods.add(method);
            }
        }
        return methods;
    }

    /**
     * 根据名称获取方法
     *
     * @param object
     * @param methodName
     * @return
     */
    public static List<Method> getDeclaredMethods(Object object, String methodName) {
        return getDeclaredMethods(object.getClass(), methodName);
    }

    /**
     * 根据名称获取方法
     *
     * @param beanClass
     * @param methodName
     * @return
     */
    public static List<Method> getDeclaredMethods(Class<?> beanClass, String methodName) {
        List<Method> methods = new ArrayList<>();
        Method[] beanMethods = beanClass.getDeclaredMethods();
        for (Method method : beanMethods) {
            if (method.getName().equals(methodName)) {
                methods.add(method);
            }
        }
        return methods;
    }

    /**
     * 获取指定参数类型的方法
     *
     * @param object
     * @param methodName
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getMethod(Object object, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        return getMethod(object.getClass(), methodName, parameterTypes);
    }

    /**
     * 获取指定参数类型的方法
     *
     * @param beanClass
     * @param methodName
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getMethod(Class<?> beanClass, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        return beanClass.getMethod(methodName, parameterTypes);
    }

    /**
     * 获取字段
     *
     * @param object
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     */
    public static Field getDeclaredField(Object object, String fieldName) throws NoSuchFieldException {
        return getDeclaredField(object.getClass(), fieldName);
    }

    /**
     * 获取字段
     *
     * @param beanClass
     * @param fieldName
     * @return
     */
    public static Field getDeclaredField(Class<?> beanClass, String fieldName) throws NoSuchFieldException {
        return beanClass.getDeclaredField(fieldName);
    }

    /**
     * 获取字段值
     *
     * @param object
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object getDeclaredFieldValue(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = getDeclaredField(object, fieldName);
        field.setAccessible(true);
        Object value = field.get(object);
        field.setAccessible(false);
        return value;
    }

    /**
     * 获取属性值
     *
     * @param object
     * @param propertyName
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static String getPropertyValue(Object object, String propertyName) throws NoSuchFieldException, IllegalAccessException {
        return getPropertyValue(object, propertyName, "");
    }

    /**
     * 获取属性值
     *
     * @param object
     * @param propertyName
     * @return
     */
    public static String getPropertyValue(Object object, String propertyName, String separator) throws NoSuchFieldException, IllegalAccessException {

        if (object == null || StringUtils.isBlank(propertyName)) {
            return "";
        }
        // 数组
        Object[] objectArray = toObjectArray(object);
        if (objectArray != null) {
            StringBuilder builder = new StringBuilder();
            for (Object o : objectArray) {
                String propertyValue = getPropertyValue(o, propertyName, separator);
                if (propertyValue != null && propertyValue.length() > 0) {
                    builder.append(propertyValue).append(separator);
                }
            }
            if (builder.length() > 0) {
                return builder.substring(0, builder.lastIndexOf(separator));
            } else {
                return "";
            }
        }

        // 属性名包含“.”，层级属性，需要递归获取属性值
        int index = propertyName.indexOf(".");
        // 第一级属性名
        String firstPropertyName = propertyName;
        String nextPropertyName = null;
        if (index > 0) {
            firstPropertyName = propertyName.substring(0, index);
            nextPropertyName = propertyName.substring(index + 1);
        }

        String regex = "[a-zA-Z0-9]+\\[[0-9]+]";
        Pattern pattern = Pattern.compile(regex);

        // 一级属性名包含数组下标，根据下标获取对应的字段值
        if (pattern.matcher(firstPropertyName).matches()) {
            String[] split = firstPropertyName.split("\\[");
            int i = Integer.valueOf(split[1].replaceAll("]", ""));
            Object firstFieldValue = getDeclaredFieldValue(object, split[0]);
            // 字段值转数组
            Object[] fieldValueArray = toObjectArray(firstFieldValue);
            if (fieldValueArray != null && i <= fieldValueArray.length - 1) {
                Object fieldValue = fieldValueArray[i];
                if (nextPropertyName == null) {
                    return toStr(fieldValue, "");
                } else {
                    return getPropertyValue(fieldValue, nextPropertyName, separator);
                }
            } else {
                return "";
            }
        } else {
            Object firstFieldValue = getDeclaredFieldValue(object, firstPropertyName);
            if (nextPropertyName == null) {
                return toStr(firstFieldValue, "");
            } else {
                return getPropertyValue(firstFieldValue, nextPropertyName, separator);
            }
        }
    }

    /**
     * object转数组，如果不是数组或者集合，则返回null
     *
     * @param object
     * @return
     */
    public static Object[] toObjectArray(Object object) {
        if (object instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) object;
            return collection.toArray();
        }
        if (ObjectUtils.isArray(object)) {
            return ObjectUtils.toObjectArray(object);
        }
        return null;
    }

    /**
     * 转字符串
     *
     * @param object
     * @param defaultValue
     * @return
     */
    public static String toStr(Object object, String defaultValue) {
        if (null == object) {
            return defaultValue;
        }
        if (object instanceof String) {
            return (String) object;
        }
        return object.toString();
    }

}
