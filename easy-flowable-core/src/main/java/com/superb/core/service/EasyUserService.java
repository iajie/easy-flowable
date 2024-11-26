package com.superb.core.service;

import com.superb.core.domain.dto.Option;
import com.superb.core.domain.entity.EasyFlowableUser;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link com.superb.core.service}
 * 公共接口
 * @author MoJie
 * @since 1.0  2024-09-27-14:02
 */
public interface EasyUserService {

    /**
     * @param userId 用户会话ID-用户ID
     * @return {@link EasyFlowableUser} 当前用户信息
     * @author MoJie
     * @since 1.0  2024/11/23 17:04
     */
    EasyFlowableUser getCurrentUser(Object userId);

    /**
     * @param username 登陆账号
     * @param password 密码
     * @return {@link Object} 自定义登陆-返回用户ID
     * @author MoJie
     * @since 1.0  2024/11/23 16:03
     */
    Object login(String username, String password);

    /**
     * @return {@link List} {@link Option} 获取用户组-如果需要使用控制台，那么您需要实现该方法
     * @author MoJie
     * @since 1.0  2024/11/9 12:46
     */
    default List<Option> users() {
        return new ArrayList<>();
    }

    /**
     * @return {@link List} {@link Option} 获取候选组-如果需要使用控制台，那么您需要实现该方法
     * @author MoJie
     * @since 1.0  2024/11/9 12:46
     */
    default List<Option> groups() {
        return new ArrayList<>();
    }

}
