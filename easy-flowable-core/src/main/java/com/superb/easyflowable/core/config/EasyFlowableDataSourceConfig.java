package com.superb.easyflowable.core.config;

import lombok.Data;

/**
 * @package: {@link com.superb.easyflowable.core.config}
 * @Date: 2024-09-26-13:27
 * @Description:
 * @Author: MoJie
 */
@Data
public class EasyFlowableDataSourceConfig {

    /** 数据库驱动 */
    private String driver;

    /** 数据库连接 */
    private String url;

    /** 数据库用户名 */
    private String username;

    /** 数据库密码 */
    private String password;

    /** 连接池能够容纳的最大活动连接数量 */
    private int maxActiveConnections = 10;

    /** 连接池能够容纳的最大空闲连接数量 */
    private int maxIdleConnections = 8;

    /** 连接从连接池“取出”后，被强制返回前的最大时间间隔，单位为毫秒 默认20秒 */
    private int maxCheckoutTime = 20000;

    /** 在连接池获取连接的时间异常长时，打印日志并尝试重新获取连接（避免连接池配置错误，导致没有异常提示）默认20秒 */
    private int maxWaitTime = 20000;

}
