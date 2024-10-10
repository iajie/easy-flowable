package com.superb.easyflowable.starter.config;

import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * @package: {@link com.superb.easyflowable.starter.config}
 * @Date: 2024-10-10-12:37
 * @Description: MybatisFlex配置
 * @Author: MoJie
 */
@Configuration
public class MybatisFlexConfiguration implements MyBatisFlexCustomizer {

    @Override
    public void customize(FlexGlobalConfig flexGlobalConfig) {
        // 不打印banner
        flexGlobalConfig.setPrintBanner(false);
        // id生成策略为uuid
        FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
        keyConfig.setKeyType(KeyType.Generator);
        keyConfig.setValue(KeyGenerators.uuid);
        flexGlobalConfig.setKeyConfig(keyConfig);
    }
}
