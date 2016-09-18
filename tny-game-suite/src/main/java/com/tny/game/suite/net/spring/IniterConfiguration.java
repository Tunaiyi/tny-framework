package com.tny.game.suite.net.spring;

import com.tny.game.suite.initer.EnumCheckerIniter;
import com.tny.game.suite.initer.ProtoExSchemaIniter;
import com.tny.game.suite.utils.Configs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
public class IniterConfiguration {

    @Bean
    @Profile({PROTOEX, SERVER, SERVER_KAFKA, GAME_KAFKA, GAME})
    public ProtoExSchemaIniter protoExSchemaIniter() {
        return new ProtoExSchemaIniter(Configs.getScanPaths());
    }

    @Bean
    public EnumCheckerIniter enumCheckerIniter() {
        return new EnumCheckerIniter(Configs.getScanPaths());
    }

}
