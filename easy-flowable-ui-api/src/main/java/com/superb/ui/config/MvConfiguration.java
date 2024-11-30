package com.superb.ui.config;

import com.superb.core.config.EasyFlowableUiConfig;
import com.superb.core.constans.Constants;
import com.superb.core.constans.EasyFlowableContext;
import com.superb.core.domain.entity.EasyFlowableUser;
import com.superb.core.domain.model.Result;
import com.superb.core.service.EasyUserService;
import com.superb.core.utils.EasyFlowableStringUtils;
import com.superb.starter.config.EasyFlowableConfigProperties;
import org.flowable.common.engine.api.FlowableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 配置静态资源访问
 * @since 1.0  2024-09-27-15:34
 * @author MoJie
 */
@Configuration
public class MvConfiguration implements WebMvcConfigurer {

    @Autowired
    private EasyFlowableConfigProperties properties;
    @Autowired
    private EasyUserService userService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(Constants.BASE_URL)
                .addResourceLocations("classpath:/easy-flowable/")
                .resourceChain(false);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/easy-flowable")
                .setViewName("redirect:/easy-flowable/index.html");
        registry.addViewController("/flowable")
                .setViewName("redirect:/easy-flowable/index.html");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String tenantId = request.getHeader(EasyFlowableContext.TENANT_ID);
                if (EasyFlowableStringUtils.isBlank(tenantId)) {
                    EasyFlowableContext.setLocal(EasyFlowableContext.TENANT_ID, tenantId);
                }
                if (properties.getUi().isLogin() && !isMatch(request.getRequestURI())) {
                    Object userId = request.getSession().getAttribute("userId");
                    if (userId == null) {
                        response.setStatus(401);
                        return false;
                    } else {
                        EasyFlowableUser currentUser = userService.getCurrentUser(userId);
                        EasyFlowableContext.setUser(currentUser);
                    }
                } else {
                    EasyFlowableUiConfig.User user = EasyFlowableUiConfig.User.defaultUser();
                    EasyFlowableContext.setUser(new EasyFlowableUser()
                            .setUserId(user.getId())
                            .setEmail("easyflowable@yeah.net")
                            .setUsername(user.getUsername())
                            .setOriginId(user.getOrganId()));
                }
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                EasyFlowableContext.clear();
                if (ex != null) {
                    Result<Object> error = Result.error(ex.getMessage());
                    if (ex instanceof FlowableException) {
                        if (ex.getMessage().equals("A delegated task cannot be completed, but should be resolved instead.")) {
                            error.setMessage("当前任务已被委托，需委托人完成任务后即可执行！");
                        }
                    }
                    response.setStatus(200);
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json");
                    response.getWriter().write(EasyFlowableStringUtils.toJson(error));
                    response.reset();
                }
            }
        }).addPathPatterns(Constants.BASE_URL);
    }

    /**
     * @param path url
     * @return {@link boolean}
     * @author MoJie
     * @since 1.0  2024/11/23 17:35
     * @description url匹配
     */
    private static boolean isMatch(String path) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String pattern : Constants.IGNORE_URLS) {
            if (antPathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}
