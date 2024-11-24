package com.superb.core.constans;

import com.superb.core.domain.entity.EasyFlowableUser;

import java.util.HashMap;
import java.util.Map;

/**
 * @package: {@link com.superb.core.constans}
 * @Date: 2024-10-11-9:47
 * @Description:
 * @Author: MoJie
 */
public class EasyFlowableContext {

    public static final String TENANT_ID = "tenantId";
    public static final String USER = "user";
    public static final String USER_ID = "userId";

    public static ThreadLocal<Map<String, Object>> local = new ThreadLocal<>();

    public static void setLocal(String key, Object value) {
        if (local.get() == null) {
            local.set(new HashMap<>());
        }
        local.get().put(key, value);
    }

    public static Object getLocal(String name) {
        Object value = null;
        if (local.get() != null && local.get().containsKey(name)) {
            value = local.get().get(name);
        }
        return value;
    }

    public static void setUser(EasyFlowableUser user) {
        setLocal(USER, user);
    }

    public static EasyFlowableUser getUser() {
        return (EasyFlowableUser)getLocal(USER);
    }

    public static String getTenantId() {
        return (String)getLocal(TENANT_ID);
    }

    public static void clear() {
        local.remove();
    }
}
