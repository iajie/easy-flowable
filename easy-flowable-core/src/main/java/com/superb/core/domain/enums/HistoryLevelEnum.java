package com.superb.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flowable.common.engine.impl.history.HistoryLevel;

/**
 * @package: {@link com.superb.core.enums}
 * @Date: 2024-09-26-11:21
 * @Description:
 * @Author: MoJie
 */
@Getter
@AllArgsConstructor
public enum HistoryLevelEnum {

    /** 这是流程执行性能最高的配置，但是不会保存任何历史信息 */
    NONE("none", "跳过所有历史的存档"),
    /** 在流程实例结束时，将顶级流程实例变量的最新值复制为历史变量实例。但不会存档细节。 */
    ACTIVITY("activity", "存档所有流程实例与活动实例"),
    /** 默认级别。将存档所有流程实例及活动实例，并保持变量值与提交的表单参数的同步，以保证所有通过表单进行的用户操作都可追踪、可审计。 */
    AUDIT("audit", "存档所有流程实例及活动实例"),
    /** 这个级别存储所有audit级别存储的信息，加上所有其他细节（主要是流程变量的更新）。 */
    FULL("full", "历史存档的最高级别，因此也最慢"),
    ;

    /** key */
    private final String key;

    /** 说明 */
    private final String description;

    /**
     * 枚举key
     * @param key 请求key
     * @return {@link String}
     * @author MoJie
     * @date 2024-09-26 11:27:08
     */
    public static HistoryLevel getKey(String key) {
        for (HistoryLevelEnum value : values()) {
            if (value.key.equals(key)) {
                return HistoryLevel.getHistoryLevelForKey(key);
            }
        }
        return HistoryLevel.AUDIT;
    }
}
