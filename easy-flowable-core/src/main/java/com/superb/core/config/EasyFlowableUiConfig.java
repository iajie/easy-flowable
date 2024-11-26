package com.superb.core.config;

import com.superb.core.domain.dto.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UI控制器参数
 * @since 1.0  2024-10-10-13:33
 * @author MoJie
 */
@Data
public class EasyFlowableUiConfig {

    /**
     * 是否需要登录
     */
    private boolean isLogin = false;

    /**
     * 用户列表
     */
    private List<User> users = Collections.emptyList();

    /**
     * 候选组列表
     */
    private List<Option> groups = Collections.emptyList();

    /**
     * 控制条用户配置
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {

        /**
         * 用户名
         */
        private String username;

        /**
         * 用户ID
         */
        private String id;

        /**
         * 用户登录密码
         */
        private String password;

        /**
         * 所属部门
         */
        private String organId;

        /**
         * 默认用户
         * @return {@link User}
         * @author MoJie
         */
        public static User defaultUser() {
            return new User("easy-flowable", "01", "easy-flowable", "222");
        }
    }

    /**
     * 默认用户组
     * @return {@link List} {@link User}
     * @author MoJie
     */
    public List<User> getUsers() {
        if (users.isEmpty()) {
            users = new ArrayList<>();
            users.add(new User("easy-flowable", "01", "easy-flowable", "222"));
            users.add(new User("甲", "123", "123456", "220"));
            users.add(new User("乙", "124", "123456", "220"));
            users.add(new User("丙", "125", "123456", "221"));
            users.add(new User("丁", "126", "123456", "222"));
        }
        return users;
    }

    /**
     * 默认组
     * @return {@link List} {@link Option}
     * @author MoJie
     */
    public List<Option> getGroups() {
        if (groups.isEmpty()) {
            groups = new ArrayList<>();
            groups.add(new Option("easy-flowable业务部", "220"));
            groups.add(new Option("easy-flowable商务部", "221"));
            groups.add(new Option("easy-flowable研发部", "222"));
        }
        return groups;
    }

}
