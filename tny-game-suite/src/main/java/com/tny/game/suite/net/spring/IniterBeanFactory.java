package com.tny.game.suite.net.spring;

import com.tny.game.suite.initer.OpLogSnapshotIniter;
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
public class IniterBeanFactory {

    @Bean
    @Profile({PROTOEX, SERVER, GAME})
    public ProtoExSchemaIniter protoExSchemaIniter() {
        return new ProtoExSchemaIniter();
    }

    // @Bean
    // public EnumCheckerIniter enumCheckerIniter() {
    //     return new EnumCheckerIniter(Configs.getScanPaths());
    // }

    @Bean
    public OpLogSnapshotIniter opLogSnapshotIniter() {
        return new OpLogSnapshotIniter(Configs.getScanPaths());
    }

}
