package com.superb.core.domain.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: MoJie
 * @Date: 2024-11-23 17:01
 * @Description: 用户信息
 */
@Data
@Accessors(chain = true)
public class EasyFlowableUser {

    /**
     * 用户ID-唯一标识
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 所属部门ID
     */
    private String originId;

}
