package com.superb.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表单项字段说明
 * @author MoJie
 * @since 1.0  2024-11-16 15:21
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EasyItem {

    /**
     * @return {@link String} 字段说明
     * @author MoJie
     * @since 1.0  2024/11/16 15:25
     */
    String name();

    /**
     * @return {@link boolean} 是否为链接
     * @author MoJie
     * @since 1.0  2024/11/16 15:25
     */
    boolean isUrl() default false;

}
