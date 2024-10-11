package com.superb.easyflowable.ui.config;

import com.superb.easyflowable.core.config.EasyFlowableUiConfig;
import com.superb.easyflowable.core.utils.StringUtils;
import com.superb.easyflowable.starter.config.EasyFlowableConfigProperties;
import com.superb.easyflowable.ui.context.EasyFlowableContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;
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
                String tenantId = request.getHeader(EasyFlowableContext.TENANT_ID);
                if (StringUtils.isBlank(tenantId)) {
                    EasyFlowableContext.setLocal(EasyFlowableContext.TENANT_ID, tenantId);
                }
                String organId = request.getHeader(EasyFlowableContext.ORGAN_ID);
                if (StringUtils.isBlank(organId)) {
                    EasyFlowableContext.setLocal(EasyFlowableContext.ORGAN_ID, organId);
                }
                if (properties.getUi().isLogin() && !"/easy-flowable/login".equals(request.getRequestURI())) {
                    Object username = request.getSession().getAttribute("username");
                    if (username == null) {
                        response.setStatus(401);
                        return false;
                    } else {
                        List<EasyFlowableUiConfig.User> users = properties.getUi().getUsers();
                        Optional<EasyFlowableUiConfig.User> first = users.stream().filter(i -> i.getUsername().equals(username)).findFirst();
                        first.ifPresent(EasyFlowableContext::setUser);
                    }
                }
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                EasyFlowableContext.clear();
            }
        }).addPathPatterns(properties.getUi().getPath() + "/**");
    }
}
