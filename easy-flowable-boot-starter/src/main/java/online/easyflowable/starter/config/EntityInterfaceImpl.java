package online.easyflowable.starter.config;

import online.easyflowable.core.domain.interfaces.EasyFlowEntityInterface;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;

/**
 * @package: {@link online.easyflowable.starter.config}
 * @Date: 2024-10-09-15:44
 * @Description:
 * @Author: MoJie
 */
@AutoConfigureBefore
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
