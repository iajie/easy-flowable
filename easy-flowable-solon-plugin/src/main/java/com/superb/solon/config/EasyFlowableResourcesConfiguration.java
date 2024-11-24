package com.superb.solon.config;

import com.superb.solon.resource.*;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @Author: MoJie
 * @Date: 2024-11-24 17:03
 * @Description:
 */
@Configuration
public class EasyFlowableResourcesConfiguration {

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
