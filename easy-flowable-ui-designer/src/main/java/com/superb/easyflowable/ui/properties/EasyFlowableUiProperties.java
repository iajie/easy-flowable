package com.superb.easyflowable.ui.properties;

import com.superb.easyflowable.core.domain.dto.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @package: {@link com.superb.easyflowable.ui.properties}
 * @Date: 2024-10-09-17:16
 * @Description:
 * @Author: MoJie
 */
@Data
@Component
@ConfigurationProperties(prefix = "easy-flowable.ui")
public class EasyFlowableUiProperties {

    /**
     * admin默认访问路径
     */
    private String path = "/easy-flowable";

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

        public static User defaultUser() {
            return new User("easy-flowable", "01", "easy-flowable", "222");
        }
    }

    public List<User> getUsers() {
        if (users.isEmpty()) {
            users.add(EasyFlowableUiProperties.User.defaultUser());
            users.add(new EasyFlowableUiProperties.User("甲", "123", "123456", "220"));
            users.add(new EasyFlowableUiProperties.User("乙", "124", "123456", "220"));
            users.add(new EasyFlowableUiProperties.User("丙", "125", "123456", "221"));
            users.add(new EasyFlowableUiProperties.User("丁", "126", "123456", "222"));
        }
        return users;
    }

    public List<Option> getGroups() {
        if (groups.isEmpty()) {
            groups.add(new Option("easy-flowable业务部", "220"));
            groups.add(new Option("easy-flowable商务部", "221"));
            groups.add(new Option("easy-flowable研发部", "222"));
        }
        return groups;
    }
}
