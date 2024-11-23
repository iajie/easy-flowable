package com.superb.ui.resource;

import com.superb.core.config.EasyFlowableUiConfig;
import com.superb.core.constans.Constants;
import com.superb.core.domain.dto.Option;
import com.superb.core.service.EasyUserService;
import com.superb.core.utils.StringUtils;
import com.superb.starter.config.EasyFlowableConfigProperties;
import com.superb.ui.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @package: {@link com.superb.ui.resource}
 * @Date: 2024-10-09-17:49
 * @Description:
 * @Author: MoJie
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
     * @return {@link List<Option>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:51:30
     */
    @GetMapping("/users")
    public Result<List<Option>> users() {
        return Result.success(userService.users());
    }

    /**
     * 获取候选组列表
     * @return {@link List<Option>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:51:30
     */
    @GetMapping("/groups")
    public Result<List<Option>> groups() {
        return Result.success(userService.groups());
    }

    /**
     * 是否登录
     * @return {@link Result<Boolean>}
     * @Author: MoJie
     * @Date: 2024-10-09 18:02:58
     */
    @GetMapping("/isLogin")
    public Result<Boolean> isLogin() {
        return Result.success(properties.getUi().isLogin());
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody EasyFlowableUiConfig.User user, HttpServletRequest request, HttpServletResponse response) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return Result.error("账号/密码不能为空");
        }
        Object userId = userService.login(username, password);
        request.getSession().setAttribute("userId", userId);
        return Result.success(true);
    }

}
