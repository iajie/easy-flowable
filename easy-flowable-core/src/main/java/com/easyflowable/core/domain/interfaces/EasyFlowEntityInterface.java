package com.easyflowable.core.domain.interfaces;

/**
 * @package: {@link com.easyflowable.core.domain.interfaces}
 * @Date: 2024-09-27-14:02
 * @Description: 公共接口
 * @Author: MoJie
 */
public interface EasyFlowEntityInterface {

    /** 当前租户 */
    default String getTenantId() {
        return null;
    }

    /** 当前部门 */
    default String getOrganId() {
        return null;
    }

    /** 当前用户 */
    String getUserId();

    /** 当前用户昵称 */
    String getUsername();

}
