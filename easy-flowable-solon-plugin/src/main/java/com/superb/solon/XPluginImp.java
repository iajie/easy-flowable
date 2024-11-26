package com.superb.solon;

import com.superb.core.constans.Constants;
import com.superb.solon.config.EasyFlowableConfiguration;
import com.superb.solon.config.EasyFlowableRouterInterceptor;
import com.superb.solon.properties.EasyFlowableConfigProperties;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;

import java.util.Properties;

/**
 * solon插件实现
 * @author MoJie
 * @since 1.0  2024-11-23 20:12
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        // 加载默认配置
        Properties defProps = Utils.loadProperties("META-INF/easy-flowable-default.properties");
        if (defProps != null && defProps.size() > 0) {
            defProps.forEach((k, v) -> context.cfg().putIfAbsent(k, v));
        }
        boolean enable = context.cfg().getBool(Constants.EASY_FLOWABLE + ".enable", true);
        if (!enable) {
            return;
        }
        context.beanMake(EasyFlowableConfigProperties.class);
        context.beanMake(EasyFlowableConfiguration.class);
        context.beanScan("com.superb.solon.api");
        context.beanScan("com.superb.solon.resource");
        context.beanMake(EasyFlowableRouterInterceptor.class);
        // 配置静态资源
        StaticMappings.add("/easy-flowable/", new ClassPathStaticRepository("/easy-flowable/"));
    }
}
