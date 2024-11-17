package com.easyflowable.ui.resource;

import com.easyflowable.core.config.EasyFlowableUiConfig;
import com.easyflowable.core.constans.Constants;
import com.easyflowable.core.domain.dto.Option;
import com.easyflowable.core.service.EasyUserService;
import com.easyflowable.core.utils.StringUtils;
import com.easyflowable.starter.config.EasyFlowableConfigProperties;
import com.easyflowable.ui.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @package: {@link com.easyflowable.ui.resource}
 * @Date: 2024-10-09-17:49
 * @Description:
 * @Author: MoJie
 */
@RestController
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
    @GetMapping(Constants.EASY_FLOWABLE + "/users")
    public Result<List<Option>> users() {
        if (!userService.users().isEmpty()) {
            return Result.success(userService.users());
        }
        List<Option> list = new ArrayList<>();
        for (EasyFlowableUiConfig.User user : properties.getUi().getUsers()) {
            list.add(new Option(user.getUsername(), user.getId()));
        }
        return Result.success(list);
    }

    /**
     * 获取候选组列表
     * @return {@link List<Option>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:51:30
     */
    @GetMapping(Constants.EASY_FLOWABLE + "/groups")
    public Result<List<Option>> groups() {
        if (!userService.groups().isEmpty()) {
            return Result.success(userService.groups());
        }
        return Result.success(properties.getUi().getGroups());
    }

    /**
     * 是否登录
     * @return {@link Result<Boolean>}
     * @Author: MoJie
     * @Date: 2024-10-09 18:02:58
     */
    @GetMapping(Constants.EASY_FLOWABLE + "/isLogin")
    public Result<Boolean> isLogin() {
        return Result.success(properties.getUi().isLogin());
    }

    @PostMapping(Constants.EASY_FLOWABLE + "/login")
    public Result<?> login(@RequestBody EasyFlowableUiConfig.User user, HttpServletRequest request) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return Result.error("账号/密码不能为空");
        }
        List<EasyFlowableUiConfig.User> users = properties.getUi().getUsers();
        Optional<EasyFlowableUiConfig.User> first = users.stream().filter(i -> i.getUsername().equals(username)).findFirst();
        if (!first.isPresent()) {
            return Result.error("账号/密码错误");
        }
        EasyFlowableUiConfig.User user1 = first.get();
        if (user1.getPassword().equals(password)) {
            request.getSession().setAttribute("username", username);
            return Result.success(true);
        }
        return Result.error("账号/密码错误");
    }

}
