package com.superb.ui;

import com.superb.ui.resource.*;
import com.superb.ui.resource.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 流程组件提供默认api配置自动装配
 * @author MoJie
 * @since 1.0  2024-11-09 13:29
 */
@Configuration
@ConditionalOnProperty(prefix = "easy-flowable", name = "enable", havingValue = "true", matchIfMissing = true)
public class EasyFlowableUiAutoConfiguration {

    /**
     * 基础接口；是否需要登录。。
     * @return {@link EasyFlowableResource}
     * @author MoJie
     */
    @Bean
    public EasyFlowableResource easyFlowableResource() {
        return new EasyFlowableResource();
    }

    /**
     * 模型相关
     * @return {@link EasyModelResource}
     * @author MoJie
     */
    @Bean
    public EasyModelResource easyModelResource() {
        return new EasyModelResource();
    }

    /**
     * 流程部署相关api
     * @return {@link EasyDeploymentResource}
     * @author MoJie
     */
    @Bean
    public EasyDeploymentResource easyDeploymentResource() {
        return new EasyDeploymentResource();
    }

    /**
     * 流程实例相关
     * @return {@link EasyProcessInstanceResource}
     * @author MoJie
     */
    @Bean
    public EasyProcessInstanceResource easyProcessInstanceResource() {
        return new EasyProcessInstanceResource();
    }

    /**
     * 流程任务相关api
     * @return {@link EasyTaskResource}
     * @author MoJie
     */
    @Bean
    public EasyTaskResource easyTaskResource() {
        return new EasyTaskResource();
    }

}
