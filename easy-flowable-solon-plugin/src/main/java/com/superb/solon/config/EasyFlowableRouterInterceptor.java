package com.superb.solon.config;

import com.superb.core.config.EasyFlowableUiConfig;
import com.superb.core.constans.Constants;
import com.superb.core.constans.EasyFlowableContext;
import com.superb.core.domain.entity.EasyFlowableUser;
import com.superb.core.domain.model.Result;
import com.superb.core.exception.EasyFlowableException;
import com.superb.core.service.EasyUserService;
import com.superb.core.utils.EasyFlowableStringUtils;
import com.superb.solon.properties.EasyFlowableConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.PathRule;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.smartboot.http.common.utils.AntPathMatcher;

/**
 * @author MoJie
 * @since 1.0  2024-11-24 11:46
 * 流程拦截器
 */
@Slf4j
@Component
public class EasyFlowableRouterInterceptor implements RouterInterceptor {

    @Inject
    private EasyFlowableConfigProperties properties;
    @Inject
    private EasyUserService userService;


    /**
     * @return {@link PathRule}
     * @author MoJie
     * @since 1.0  2024/11/24 11:50
     *  自定义拦截规则
     */
    @Override
    public PathRule pathPatterns() {
        return new PathRule().include(Constants.BASE_URL, "/" + Constants.EASY_FLOWABLE);
    }

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        try {
            if (ctx.path().equals("/" + Constants.EASY_FLOWABLE)) {
                ctx.redirect("/" + Constants.EASY_FLOWABLE + "/index.html");
                return;
            }
            String tenantId = ctx.header(EasyFlowableContext.TENANT_ID);
            if (EasyFlowableStringUtils.isBlank(tenantId)) {
                EasyFlowableContext.setLocal(EasyFlowableContext.TENANT_ID, tenantId);
            }
            if (properties.getUi().isLogin() && !isMatch(ctx.path())) {
                Object userId = ctx.session(EasyFlowableContext.USER_ID);
                if (userId == null) {
                    ctx.status(401);
                } else {
                    EasyFlowableContext.setUser(userService.getCurrentUser(userId));
                }
            } else {
                EasyFlowableUiConfig.User user = EasyFlowableUiConfig.User.defaultUser();
                EasyFlowableContext.setUser(new EasyFlowableUser()
                        .setUserId(user.getId())
                        .setEmail("easyflowable@yeah.net")
                        .setUsername(user.getUsername())
                        .setOriginId(user.getOrganId()));
            }
            chain.doIntercept(ctx, mainHandler);
        } catch (EasyFlowableException ex) {
            log.error("easy-flowable异常", ex);
            ctx.render(Result.error(ex.getMessage()));
        } catch (Exception ex) {
            log.error("程序错误: {}", ctx.path(), ex);
            ctx.render(Result.error("程序异常", ex.getMessage()));
        }
    }

    @Override
    public Object postResult(Context ctx, Object result) throws Throwable {
        EasyFlowableContext.clear();
        return RouterInterceptor.super.postResult(ctx, result);
    }

    /**
     * @param path url
     * @return {@link boolean}
     * @author MoJie
     * @since 1.0  2024/11/23 17:35
     *  url匹配
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
