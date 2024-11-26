package com.superb.solon.api;

import com.superb.core.config.EasyFlowableUiConfig;
import com.superb.core.domain.dto.Option;
import com.superb.core.domain.entity.EasyFlowableUser;
import com.superb.core.exception.EasyFlowableException;
import com.superb.core.service.EasyUserService;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author MoJie
 * @since 1.0  2024-11-22 21:15
 * 默认对用户的实现
 */
@AllArgsConstructor
public class DefaultEasyUserServiceImpl implements EasyUserService {

    private final EasyFlowableUiConfig properties;

    @Override
    public EasyFlowableUser getCurrentUser(Object userId) {
        EasyFlowableUser user = new EasyFlowableUser();
        Optional<EasyFlowableUiConfig.User> first = properties.getUsers().stream().filter(i -> i.getId().equals(userId)).findFirst();
        if (!first.isPresent()) {
            return user;
        }
        EasyFlowableUiConfig.User uiUser = first.get();
        user.setUserId(userId.toString());
        user.setEmail("easyflowable@yeah.net");
        user.setOriginId(uiUser.getOrganId());
        user.setUsername(uiUser.getUsername());
        return user;
    }

    @Override
    public Object login(String username, String password) {
        List<EasyFlowableUiConfig.User> users = properties.getUsers();
        Optional<EasyFlowableUiConfig.User> first = users.stream().filter(i -> i.getUsername().equals(username)).findFirst();
        if (!first.isPresent()) {
            throw new EasyFlowableException("账号/密码错误");
        }
        EasyFlowableUiConfig.User user1 = first.get();
        if (!user1.getPassword().equals(password)) {
            throw new EasyFlowableException("账号/密码错误");
        }
        return user1.getId();
    }

    @Override
    public List<Option> users() {
        List<Option> list = new ArrayList<>();
        for (EasyFlowableUiConfig.User user : properties.getUsers()) {
            list.add(new Option(user.getUsername(), user.getId()));
        }
        return list;
    }

    @Override
    public List<Option> groups() {
        return properties.getGroups();
    }
}
