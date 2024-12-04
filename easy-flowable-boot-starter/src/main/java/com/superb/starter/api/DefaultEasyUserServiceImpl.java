package com.superb.starter.api;

import com.superb.core.config.EasyFlowableUiConfig;
import com.superb.core.domain.dto.Option;
import com.superb.core.domain.entity.EasyFlowableUser;
import com.superb.core.exception.EasyFlowableException;
import com.superb.core.service.EasyUserService;
import com.superb.starter.config.EasyFlowableConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 默认对用户的实现
 * @author MoJie
 * @since 1.0  2024-11-22 21:15
 */
@Service
@ConditionalOnMissingBean(EasyUserService.class)
public class DefaultEasyUserServiceImpl implements EasyUserService {

    @Autowired(required = false)
    private EasyFlowableConfigProperties properties;

    @Override
    public EasyFlowableUser getCurrentUser(Object userId) {
        EasyFlowableUser user = new EasyFlowableUser();
        Optional<EasyFlowableUiConfig.User> first = properties.getUi().getUsers().stream().filter(i -> i.getId().equals(userId)).findFirst();
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
        List<EasyFlowableUiConfig.User> users = properties.getUi().getUsers();
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
        for (EasyFlowableUiConfig.User user : properties.getUi().getUsers()) {
            list.add(new Option(user.getUsername(), user.getId()));
        }
        return list;
    }

    @Override
    public List<Option> groups() {
        return properties.getUi().getGroups();
    }
}
