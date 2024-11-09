package com.easyflowable.core.service;

import com.easyflowable.core.domain.dto.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * @package: {@link com.easyflowable.core.service}
 * @Date: 2024-09-27-14:02
 * @Description: 公共接口
 * @Author: MoJie
 */
public interface EasyUserService {

    /** 当前用户 */
    String getUserId();

    /** 当前用户昵称 */
    String getUsername();

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
