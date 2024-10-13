package online.easyflowable.ui.controller;

import online.easyflowable.core.config.EasyFlowableUiConfig;
import online.easyflowable.core.domain.dto.Option;
import online.easyflowable.core.utils.StringUtils;
import online.easyflowable.starter.config.EasyFlowableConfigProperties;
import online.easyflowable.ui.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

/**
 * @package: {@link online.easyflowable.ui.controller}
 * @Date: 2024-10-09-17:49
 * @Description:
 * @Author: MoJie
 */
@RestController
@RequestMapping("easy-flowable")
public class EasyFlowableController {

    @Autowired
    private EasyFlowableConfigProperties properties;

    /**
     * 获取用户列表
     * @return {@link List<Option>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:51:30
     */
    @GetMapping("/users")
    public Result<List<Option>> users() {
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
    @GetMapping("/groups")
    public Result<List<Option>> groups() {
        return Result.success(properties.getUi().getGroups());
    }

    /**
     * 是否登录
     * @return {@link Result<Boolean>}
     * @Author: MoJie
     * @Date: 2024-10-09 18:02:58
     */
    @GetMapping("isLogin")
    public Result<Boolean> isLogin() {
        return Result.success(properties.getUi().isLogin());
    }

    @PostMapping("login")
    public Result<?> login(@RequestBody EasyFlowableUiConfig.User user, HttpServletRequest request) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return Result.error("账号/密码不能为空");
        }
        List<EasyFlowableUiConfig.User> users = properties.getUi().getUsers();
        Optional<EasyFlowableUiConfig.User> first = users.stream().filter(i -> i.getUsername().equals(username)).findFirst();
        if (first.isEmpty()) {
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
