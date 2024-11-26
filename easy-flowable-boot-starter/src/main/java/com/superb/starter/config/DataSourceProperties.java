package com.superb.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 数据源获取-存在问题
 * @author MoJie
 * @since 1.0  2024-11-24 10:55
 */
@Data
@ConfigurationProperties("spring.datasource")
public class DataSourceProperties {

    /**
     * Fully qualified name of the JDBC driver. Auto-detected based on the URL by default.
     */
    private String driverClassName;

    /**
     * JDBC URL of the database.
     */
    private String url;

    /**
     * Login username of the database.
     */
    private String username;

    /**
     * Login password of the database.
     */
    private String password;

}
