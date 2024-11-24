package com.superb.core.config;

import com.superb.core.utils.EasyFlowableStringUtils;
import lombok.Data;
import org.flowable.common.engine.impl.cfg.mail.MailServerInfo;
import org.flowable.common.engine.impl.history.HistoryLevel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @package: {@link com.superb.core.config}
 * @Date: 2024-09-26-10:46
 * @Description: es-flowable全局配置
 * @Author: MoJie
 */
@Data
public class EasyFlowableConfig {

    /**
     * 设置流程引擎启动关闭时使用的数据库表结构控制策略：
     * false: 当引擎启动时，检查数据库表结构的版本是否匹配库文件版本。版本不匹配时抛出异常
     * true(默认): 构建引擎时，检查并在需要时更新表结构。表结构不存在则会创建
     */
    private boolean tableSchema = true;

    /**
     * AsyncExecutor是管理线程池的组件，用于触发定时器与其他异步任务；定时任务开关job
     */
    private boolean asyncExecutorActivate = false;

    /** 流程历史级别 */
    private String historyLevel = HistoryLevel.AUDIT.getKey();
//
//    /** 邮件配置开关，默认关闭 */
//    private boolean isMail = false;
//
//    /** easy-flowable邮件配置 */
//    private Map<String, MailConfig> mailConfig = Collections.singletonMap("easy-flowable", new MailConfig());
//
//    /**
//     * 获取邮件配置
//     * @return {@link MailServerInfo}
//     * @Author MoJie
//     * @Date 2024-09-26 15:26:16
//     */
//    public Map<String, MailServerInfo> getMailConfig() {
//        Map<String, MailServerInfo> map = new HashMap<String, MailServerInfo>();
//        for (String key : this.mailConfig.keySet()) {
//            map.put(key, this.mailConfig.get(key).getMail());
//        }
//        return map;
//    }

    /**
     * easy-flowable邮件配置
     */
    @Data
    public static class MailConfig {

        /** 邮件服务器的主机名（如smtp.qq.com） */
        private String host;

        /** 邮件服务器的SMTP端口 */
        private int port;

        /** 发送邮箱地址 */
        private String username;

        /** 发送邮件授权码 */
        private String password;

        /** 是否开启ssl通信 */
        private boolean useSSL = false;

        /** 是否开启TLS通信 */
        private boolean useTls = false;

        private MailConfig defaultMail() {
            MailConfig config = new MailConfig();
            config.setHost("smtp.yeah.net");
            config.setUsername("easyflowable@yeah.net");
            config.setPort(465);
            config.setPassword("ZQcYwePNF3jcbaKA");
            config.setUseSSL(true);
            config.setUseTls(false);
            return config;
        }

        public MailServerInfo getMail() {
            MailServerInfo info = new MailServerInfo();
            if (EasyFlowableStringUtils.isBlank(this.username)) {
                MailConfig config = this.defaultMail();
                info.setMailServerHost(config.host);
                info.setMailServerPort(config.port);
                info.setMailServerUsername(config.username);
                info.setMailServerPassword(config.password);
                info.setMailServerUseSSL(config.useSSL);
                info.setMailServerUseTLS(config.useTls);
                return info;
            }
            info.setMailServerHost(this.host);
            info.setMailServerPort(this.port);
            info.setMailServerPassword(this.password);
            info.setMailServerUseSSL(this.useSSL);
            info.setMailServerUseTLS(this.useTls);
            info.setMailServerUsername(this.username);
            return info;
        }
    }
}
