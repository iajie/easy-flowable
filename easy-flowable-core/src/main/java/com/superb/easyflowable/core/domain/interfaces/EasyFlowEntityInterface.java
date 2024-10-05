package com.superb.easyflowable.core.domain.interfaces;

/**
 * @package: {@link com.superb.easyflowable.core.domain.interfaces}
 * @Date: 2024-09-27-14:02
 * @Description: 公共接口
 * @Author: MoJie
 */
public interface EasyFlowEntityInterface {

    /** 当前租户 */
    default String getCurrentTenant() {
        return null;
    }

    /** 当前部门 */
    default String getCurrentOrganId() {
        return null;
    }

    /** 当前用户 */
    default String getCurrentUserId() {
        return null;
    }

}
