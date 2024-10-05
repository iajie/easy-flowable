package com.superb.esayflowable.ui.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @package: {@link com.superb.esayflowable.ui.config}
 * @Date: 2024-09-27-15:34
 * @Description: 配置静态资源访问
 * @Author: MoJie
 */
@Configuration
public class MvConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/easy-flowable/**").addResourceLocations("classpath:/static/", "classpath:/static/flowable/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/easy-flowable").setViewName("redirect:/easy-flowable/index.html");
        registry.addViewController("/easy-flowable").setViewName("redirect:/easy-flowable/index.html");
        registry.addViewController("/flowable/index.html").setViewName("redirect:/easy-flowable/index.html");
    }
}
