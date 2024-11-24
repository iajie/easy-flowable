package com.superb.core.utils;

import com.superb.core.annotation.EasyItem;
import com.superb.core.annotation.EasyIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @package: {@link com.superb.core.utils}
 * @Date: 2024-09-26-13:58
 * @Description:
 * @Author: MoJie
 */
public class EasyFlowableStringUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static boolean isEmpty(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs != null) {
            int length = cs.length();
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * @param str 字符串
     * @Return: {@link boolean}
     * @Author: MoJie
     * @Date: 2024/10/28 20:32
     * @Description: 判断字符串是否为json
     */
    public static boolean isJson(String str) {
        try {
            OBJECT_MAPPER.readTree(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param value 实体
     * @Return: {@link String}
     * @Author: MoJie
     * @Date: 2024/10/29 19:20
     * @Description: 将实体转为json
     */
    @SneakyThrows
    public static <T> String toJson(T value) {
        return OBJECT_MAPPER.writeValueAsString(value);
    }

    /**
     * @param jsonStr json字符串
     * @param clazz 转换类
     * @Return: {@link T}
     * @Author: MoJie
     * @Date: 2024/10/29 19:21
     * @Description: 将json字符串转为实体
     */
    @SneakyThrows
    public static <T> T toJava(String jsonStr, Class<T> clazz) {
        return OBJECT_MAPPER.readValue(jsonStr, clazz);
    }

    /**
     * @param jsonStr 字符串
     * @param valueTypeRef 泛型
     * @return: {@link T}
     * @Author: MoJie
     * @Date: 2024/11/17 12:36
     * @Description: 将字符串转实体
     */
    @SneakyThrows
    public static <T> T toJava(String jsonStr, TypeReference<T> valueTypeRef) {
        return OBJECT_MAPPER.readValue(jsonStr, valueTypeRef);
    }

    /**
     * @param jsonStr json字符串
     * @return: {@link Map} {@link Object}
     * @Author: MoJie
     * @Date: 2024/11/17 12:37
     * @Description: 将json字符串转为map对象
     */
    @SneakyThrows
    public static Map<String, Object> toMap(String jsonStr) {
        return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * @param tarClass 变化实体
     * @param scrClass 旧实体
     * @return: {@link Map} {@link Object}
     * @Author: MoJie
     * @Date: 2024/11/16 15:28
     * @Description: 记录两个实体字段发生的变化
     */
    @SneakyThrows
    public static Map<String, Object> screenTwoProperty(Object tarClass, Object scrClass) {
        // 防止线程冲突，给map加锁即使用ConcurrentHashMap
        Map<String, Object> map = new ConcurrentHashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Class<?> tarClassClass = tarClass.getClass();
        Field[] fields = tarClassClass.getDeclaredFields();
        for (Field field : fields) {
            Map<String, Object> content = new HashMap<>();
            field.setAccessible(true);
            String fieldKey = field.getName();
            Object tarValue = field.get(tarClass);
            if (!field.isAnnotationPresent(EasyIgnore.class)) {
                // 字段属性
                content.put("properties", fieldKey);
                // 记录传递的的值
                map.put(fieldKey, tarValue);
                // 如果存在注解，则获取注解上的说明
                if (field.isAnnotationPresent(EasyItem.class)) {
                    EasyItem item = field.getAnnotation(EasyItem.class);
                    content.put("name", item.name());
                    content.put("isUrl", item.isUrl());
                    content.put("newValue", tarValue);
                    if (scrClass != null) {
                        Object srcValue = field.get(scrClass);
                        if (tarValue != null && !tarValue.equals(srcValue)) {
                            content.put("oldValue", srcValue);
                        }
                    }
                    contents.add(content);
                }
            }
        }
        if (contents.size() > 0) {
            map.put("content", contents);
        }
        return map;
    }

    /**
     * @param obj 实体
     * @return: {@link boolean}
     * @Author: MoJie
     * @Date: 2024/11/17 12:28
     * @Description: 字段是否存在@EasyItem注解
     */
    public static boolean isAnnotationEasyItem(Object obj) {
        List<Field> list = new ArrayList<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(EasyItem.class)) {
                list.add(field);
            }
        }
        return list.size() > 0;
    }

}
