package com.easyflowable.ui.config;

import com.easyflowable.core.config.EasyFlowableUiConfig;
import com.easyflowable.core.utils.StringUtils;
import com.easyflowable.starter.config.EasyFlowableConfigProperties;
import com.easyflowable.ui.context.EasyFlowableContext;
import com.easyflowable.ui.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.common.engine.api.FlowableException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

/**
 * @package: {@link com.easyflowable.ui.config}
 * @Date: 2024-09-27-15:34
 * @Description: 配置静态资源访问
 * @Author: MoJie
 */
@EnableWebMvc
@Configuration
@ConditionalOnProperty(prefix = "easy-flowable", name = "enable", havingValue = "true", matchIfMissing = true)
public class MvConfiguration implements WebMvcConfigurer {

    @Resource
    private EasyFlowableConfigProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(properties.getUi().getPath() + "/**")
                .addResourceLocations("classpath:/META-INF/resources/easy-flowable/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(properties.getUi().getPath())
                .setViewName("redirect:" + properties.getUi().getPath() + "/index.html");
        registry.addViewController("/flowable/index.html")
                .setViewName("redirect:" + properties.getUi().getPath() + "/index.html");
        registry.addViewController("/flowable")
                .setViewName("redirect:" + properties.getUi().getPath() + "/index.html");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String tenantId = request.getHeader(EasyFlowableContext.TENANT_ID);
                if (StringUtils.isNotBlank(tenantId)) {
                    EasyFlowableContext.setLocal(EasyFlowableContext.TENANT_ID, tenantId);
                }
                String organId = request.getHeader(EasyFlowableContext.ORGAN_ID);
                if (StringUtils.isNotBlank(organId)) {
                    EasyFlowableContext.setLocal(EasyFlowableContext.ORGAN_ID, organId);
                }
                if (properties.getUi().isLogin() && !(properties.getUi().getPath() + "/login").equals(request.getRequestURI())) {
                    Object username = request.getSession().getAttribute("username");
                    if (username == null) {
                        response.setStatus(401);
                        return false;
                    } else {
                        List<EasyFlowableUiConfig.User> users = properties.getUi().getUsers();
                        Optional<EasyFlowableUiConfig.User> first = users.stream().filter(i -> i.getUsername().equals(username)).findFirst();
                        first.ifPresent(EasyFlowableContext::setUser);
                    }
                } else {
                    EasyFlowableContext.setUser(EasyFlowableUiConfig.User.defaultUser());
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
        }).addPathPatterns(properties.getUi().getPath() + "/**");
    }
}
