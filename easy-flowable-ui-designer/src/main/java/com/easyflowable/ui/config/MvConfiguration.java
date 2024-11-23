package com.easyflowable.ui.config;

import com.easyflowable.core.config.EasyFlowableUiConfig;
import com.easyflowable.core.constans.EasyFlowableContext;
import com.easyflowable.core.domain.entity.EasyFlowableUser;
import com.easyflowable.core.service.EasyUserService;
import com.easyflowable.core.utils.StringUtils;
import com.easyflowable.starter.config.EasyFlowableConfigProperties;
import com.easyflowable.ui.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.common.engine.api.FlowableException;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * @package: {@link com.easyflowable.ui.config}
 * @Date: 2024-09-27-15:34
 * @Description: 配置静态资源访问
 * @Author: MoJie
 * 源码打包后需要加以下logo
 * <link rel="icon" href="/easy-flowable/favicon.ico" type="image/x-icon">
 * <link rel="shortcut icon" href="/easy-flowable/favicon.ico" type="image/x-icon">
 */
@Configuration
public class MvConfiguration implements WebMvcConfigurer {

    @Resource
    private EasyFlowableConfigProperties properties;
    @Resource
    private EasyUserService userService;

    private static final String baseUrl = "/easy-flowable/**";

    private static final List<String> urls = Arrays.asList("/easy-flowable/index.html",
            "/easy-flowable/login", "/easy-flowable/favicon.ico", "/easy-flowable/*.css",
            "/easy-flowable/*.js", "/easy-flowable/static/**");

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(baseUrl)
                .addResourceLocations("classpath:/META-INF/resources/easy-flowable/")
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
                if (StringUtils.isBlank(tenantId)) {
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
                    PrintWriter writer = response.getWriter();
                    writer.write(new ObjectMapper().writeValueAsString(error));
                    writer.flush();
                    writer.close();
                }
            }
        }).addPathPatterns(baseUrl);
    }

    /**
     * @param path url
     * @return: {@link boolean}
     * @Author: MoJie
     * @Date: 2024/11/23 17:35
     * @Description: url匹配
     */
    private static boolean isMatch(String path) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String pattern : urls) {
            if (antPathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}
