package com.superb.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: MoJie
 * @Date: 2024-11-16 15:21
 * @Description: 表单项字段说明
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EasyItem {

    /**
     * @return: {@link String}
     * @Author: MoJie
     * @Date: 2024/11/16 15:25
     * @Description: 字段说明
     */
    String name();

    /**
     * @return: {@link boolean}
     * @Author: MoJie
     * @Date: 2024/11/16 15:25
     * @Description: 是否为链接
     */
    boolean isUrl() default false;

}
