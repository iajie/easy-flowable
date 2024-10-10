package com.superb.easyflowable.ui.config;

import com.superb.easyflowable.core.exception.EasyFlowableException;
import com.superb.easyflowable.starter.config.EasyFlowableConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @package: {@link com.superb.easyflowable.ui.config}
 * @Date: 2024-09-27-15:34
 * @Description: 配置静态资源访问
 * @Author: MoJie
 */
@Configuration
public class MvConfiguration implements WebMvcConfigurer {

    @Autowired
    private EasyFlowableConfigProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(properties.getUi().getPath() + "/**")
                .addResourceLocations("classpath:/static/", "classpath:/static/flowable/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/easy-flowable")
                .setViewName("redirect:/" + properties.getUi().getPath() + "/index.html");
        registry.addViewController("/flowable/index.html")
                .setViewName("redirect:/" + properties.getUi().getPath() + "/index.html");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (properties.getUi().isLogin()) {
                    Object username = request.getSession().getAttribute("username");
                    if (username == null) {
                        throw new EasyFlowableException("会话过期，请登录");
                    }
                }
                return true;
            }
        }).addPathPatterns(properties.getUi().getPath() + "/**");
    }
}
