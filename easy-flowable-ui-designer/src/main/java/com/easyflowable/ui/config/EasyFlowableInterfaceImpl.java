package com.easyflowable.ui.config;

import com.easyflowable.core.config.EasyFlowableUiConfig;
import com.easyflowable.core.domain.interfaces.EasyFlowEntityInterface;
import com.easyflowable.core.utils.StringUtils;
import com.easyflowable.ui.context.EasyFlowableContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @package: {@link com.easyflowable.ui.config}
 * @Date: 2024-10-11-9:45
 * @Description: 自定义实现信息获取
 * @Author: MoJie
 */
@Primary
@Component
public class EasyFlowableInterfaceImpl implements EasyFlowEntityInterface {

    @Override
    public String getTenantId() {
        return EasyFlowableContext.getTenantId();
    }

    @Override
    public String getOrganId() {
        EasyFlowableUiConfig.User user = EasyFlowableContext.getUser();
        if (user != null && StringUtils.isNotBlank(user.getOrganId())) {
            return user.getOrganId();
        }
        return EasyFlowableContext.getOrganId();
    }

    @Override
    public String getUserId() {
        EasyFlowableUiConfig.User user = EasyFlowableContext.getUser();
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    @Override
    public String getUsername() {
        EasyFlowableUiConfig.User user = EasyFlowableContext.getUser();
        if (user != null) {
            return user.getUsername();
        }
        return null;
    }
}
