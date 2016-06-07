package com.tny.game.suite.net.spring;

import com.tny.game.suite.initer.ProtoExSchemaIniter;
import com.tny.game.suite.utils.Configs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@Profile({"suite.server", "suite.all", "suite.protoex"})
public class ProtoExConfiguration {

    @Bean
    public ProtoExSchemaIniter protoExSchemaIniter() {
        return new ProtoExSchemaIniter(Configs.getScanPaths());
    }

}
