package com.superb.solon.resource;

import com.superb.core.config.EasyFlowableUiConfig;
import com.superb.core.constans.Constants;
import com.superb.core.domain.dto.Option;
import com.superb.core.domain.model.Result;
import com.superb.core.service.EasyUserService;
import com.superb.core.utils.EasyFlowableStringUtils;
import com.superb.solon.properties.EasyFlowableConfigProperties;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

import java.util.List;

/**
 * @package: {@link com.superb.ui.resource}
 * @Date: 2024-10-09-17:49
 * @Description:
 * @Author: MoJie
 */
@Controller
@Mapping(Constants.EASY_FLOWABLE)
public class EasyFlowableResource {

    @Inject
    private EasyFlowableConfigProperties properties;
    @Inject
    private EasyUserService userService;

    /**
     * 获取用户列表
     * @return {@link List<Option>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:51:30
     */
    @Mapping(value = "/users", method = MethodType.GET)
    public Result<List<Option>> users() {
        return Result.success(userService.users());
    }

    /**
     * 获取候选组列表
     * @return {@link List<Option>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:51:30
     */
    @Mapping(value = "/groups", method = MethodType.GET)
    public Result<List<Option>> groups() {
        return Result.success(userService.groups());
    }

    /**
     * 是否登录
     * @return {@link Result<Boolean>}
     * @Author: MoJie
     * @Date: 2024-10-09 18:02:58
     */
    @Mapping(value = "/isLogin", method = MethodType.GET)
    public Result<Boolean> isLogin() {
        return Result.success(properties.getUi().isLogin());
    }

    @Mapping(value = "/login", method = MethodType.POST)
    public Result<?> login(@Body EasyFlowableUiConfig.User user, Context ctx) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (EasyFlowableStringUtils.isBlank(username) || EasyFlowableStringUtils.isBlank(password)) {
            return Result.error("账号/密码不能为空");
        }
        Object userId = userService.login(username, password);
        ctx.sessionSet("userId", userId);
        return Result.success(true);
    }

}