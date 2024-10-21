package com.easyflowable.ui.context;

import com.easyflowable.core.config.EasyFlowableUiConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @package: {@link com.easyflowable.ui.context}
 * @Date: 2024-10-11-9:47
 * @Description:
 * @Author: MoJie
 */
public class EasyFlowableContext {

    public static final String TENANT_ID = "tenantId";
    public static final String USER = "user";
    public static final String ORGAN_ID = "organId";

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

    public static void setUser(EasyFlowableUiConfig.User user) {
        setLocal(USER, user);
    }

    public static EasyFlowableUiConfig.User getUser() {
        return (EasyFlowableUiConfig.User)getLocal(USER);
    }

    public static String getTenantId() {
        return (String)getLocal(TENANT_ID);
    }

    public static String getOrganId() {
        return (String)getLocal(ORGAN_ID);
    }

    public static void clear() {
        local.remove();
    }
}
