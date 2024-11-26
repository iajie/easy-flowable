package com.superb.ui.resource;

import com.superb.core.config.EasyFlowableUiConfig;
import com.superb.core.constans.Constants;
import com.superb.core.domain.dto.Option;
import com.superb.core.domain.model.Result;
import com.superb.core.service.EasyUserService;
import com.superb.core.utils.EasyFlowableStringUtils;
import com.superb.starter.config.EasyFlowableConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 流程提供接口
 * @since 1.0  2024-10-09-17:49
 * @author MoJie
 */
@RestController
@RequestMapping(Constants.EASY_FLOWABLE)
public class EasyFlowableResource {

    @Autowired(required = false)
    private EasyFlowableConfigProperties properties;
    @Autowired(required = false)
    private EasyUserService userService;

    /**
     * 获取用户列表
     * @return {@link Result} {@link List} {@link Option}
     * @author MoJie
     */
    @GetMapping("/users")
    public Result<List<Option>> users() {
        return Result.success(userService.users());
    }

    /**
     * 获取候选组列表
     * @return {@link Result} {@link List} {@link Option}
     * @author MoJie
     */
    @GetMapping("/groups")
    public Result<List<Option>> groups() {
        return Result.success(userService.groups());
    }

    /**
     * 是否登录
     * @return {@link Result<Boolean>}
     * @author MoJie
     * @since 1.0  2024-10-09 18:02:58
     */
    @GetMapping("/isLogin")
    public Result<Boolean> isLogin() {
        return Result.success(properties.getUi().isLogin());
    }

    /**
     * 登录接口
     * @param user 登录实体
     * @param request 请求对象
     * @return {@link Result}
     * @author MoJie
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody EasyFlowableUiConfig.User user, HttpServletRequest request) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (EasyFlowableStringUtils.isBlank(username) || EasyFlowableStringUtils.isBlank(password)) {
            return Result.error("账号/密码不能为空");
        }
        Object userId = userService.login(username, password);
        request.getSession().setAttribute("userId", userId);
        return Result.success(true);
    }

}
