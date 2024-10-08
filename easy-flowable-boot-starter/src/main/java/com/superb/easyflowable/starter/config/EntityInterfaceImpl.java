package com.superb.easyflowable.starter.config;

import com.superb.easyflowable.core.domain.interfaces.EasyFlowEntityInterface;

/**
 * @package: {@link com.superb.easyflowable.starter.config}
 * @Date: 2024-10-09-15:44
 * @Description:
 * @Author: MoJie
 */
public class EntityInterfaceImpl implements EasyFlowEntityInterface {

    @Override
    public String getTenantId() {
        return "easy-flowable";
    }

    @Override
    public String getUserId() {
        return "easy-flowable-admin";
    }

    @Override
    public String getUsername() {
        return "easy-flowable-管理员";
    }
}
