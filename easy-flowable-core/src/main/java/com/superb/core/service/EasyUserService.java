package com.superb.core.service;

import com.superb.core.domain.dto.Option;
import com.superb.core.domain.entity.EasyFlowableUser;

import java.util.ArrayList;
import java.util.List;

/**
 * @package: {@link com.superb.core.service}
 * @Date: 2024-09-27-14:02
 * @Description: 公共接口
 * @Author: MoJie
 */
public interface EasyUserService {

    /**
     * @param userId 用户会话ID-用户ID
     * @return: {@link EasyFlowableUser}
     * @Author: MoJie
     * @Date: 2024/11/23 17:04
     * @Description: 当前用户信息
     */
    EasyFlowableUser getCurrentUser(Object userId);

    /**
     * @param username 登陆账号
     * @param password 密码
     * @return: {@link Object}
     * @Author: MoJie
     * @Date: 2024/11/23 16:03
     * @Description: 自定义登陆-返回用户ID
     */
    Object login(String username, String password);

    /**
     * @return: {@link List} {@link Option}
     * @Author: MoJie
     * @Date: 2024/11/9 12:46
     * @Description: 获取用户组-如果需要使用控制台，那么您需要实现该方法
     */
    default List<Option> users() {
        return new ArrayList<>();
    }

    /**
     * @return: {@link List} {@link Option}
     * @Author: MoJie
     * @Date: 2024/11/9 12:46
     * @Description: 获取候选组-如果需要使用控制台，那么您需要实现该方法
     */
    default List<Option> groups() {
        return new ArrayList<>();
    }

}
