package com.easyflowable.core.utils;

import com.easyflowable.core.domain.dto.FlowComment;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * @package: {@link com.easyflowable.core.utils}
 * @Date: 2024-09-26-13:58
 * @Description:
 * @Author: MoJie
 */
public class StringUtils {

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
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.readTree(str);
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
        return new ObjectMapper().writeValueAsString(value);
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
        return new ObjectMapper().readValue(jsonStr, clazz);
    }

}
