package com.easyflowable.ui;

import com.easyflowable.core.service.EasyUserService;
import com.easyflowable.starter.EasyFlowableAutoConfiguration;
import com.easyflowable.ui.config.EasyFlowableInterfaceImpl;
import com.easyflowable.ui.config.MvConfiguration;
import com.easyflowable.ui.resource.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: MoJie
 * @Date: 2024-11-09 13:29
 * @Description:
 */
@Configuration
@ConditionalOnProperty(prefix = "easy-flowable", name = "enable", havingValue = "true", matchIfMissing = true)
public class EasyFlowableUiAutoConfiguration {

    @Bean
    public MvConfiguration mvConfiguration() {
        return new MvConfiguration();
    }

    @Bean
    public EasyUserService easyUserService() {
        return new EasyFlowableInterfaceImpl();
    }

    @Bean
    public EasyFlowableResource easyFlowableResource() {
        return new EasyFlowableResource();
    }

    @Bean
    public EasyModelResource easyModelResource() {
        return new EasyModelResource();
    }

    @Bean
    public EasyDeploymentResource easyDeploymentResource() {
        return new EasyDeploymentResource();
    }

    @Bean
    public EasyProcessInstanceResource easyProcessInstanceResource() {
        return new EasyProcessInstanceResource();
    }

    @Bean
    public EasyTaskResource easyTaskResource() {
        return new EasyTaskResource();
    }

}
