package com.superb.core.constans;

import com.superb.core.domain.entity.EasyFlowableUser;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0  2024-10-11-9:47
 * @author MoJie
 */
public class EasyFlowableContext {

    /**
     * 租户ID
     * 1.0未启用
     */
    public static final String TENANT_ID = "tenantId";
    /**
     * 用户
     */
    public static final String USER = "user";
    /**
     * 用户ID
     */
    public static final String USER_ID = "userId";

    /**
     * 线程变量
     */
    public static ThreadLocal<Map<String, Object>> local = new ThreadLocal<>();

    /**
     * 设置线程变量
     * @param key 键
     * @param value 值
     * @author MoJie
     */
    public static void setLocal(String key, Object value) {
        if (local.get() == null) {
            local.set(new HashMap<>());
        }
        local.get().put(key, value);
    }

    /**
     * 获取线程变量
     * @param name key
     * @return {@link Object}
     * @author MoJie
     */
    public static Object getLocal(String name) {
        Object value = null;
        if (local.get() != null && local.get().containsKey(name)) {
            value = local.get().get(name);
        }
        return value;
    }

    /**
     * 设置用户信息
     * @param user 用户变量
     * @author MoJie
     */
    public static void setUser(EasyFlowableUser user) {
        setLocal(USER, user);
    }

    /**
     * 获取用户变量
     * @return {@link EasyFlowableUser}
     * @author MoJie
     */
    public static EasyFlowableUser getUser() {
        return (EasyFlowableUser)getLocal(USER);
    }

    /**
     * 获取当前租户
     * @return {@link String}
     * @author MoJie
     */
    public static String getTenantId() {
        return (String)getLocal(TENANT_ID);
    }

    /**
     * 清除线程变量
     */
    public static void clear() {
        local.remove();
    }
}
