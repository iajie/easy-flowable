package com.easyflowable.ui;

import com.easyflowable.ui.resource.*;
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
