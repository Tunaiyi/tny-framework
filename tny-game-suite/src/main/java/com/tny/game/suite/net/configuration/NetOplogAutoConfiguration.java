package com.tny.game.suite.net.configuration;

import com.tny.game.suite.initer.OpLogSnapshotIniter;
import com.tny.game.suite.utils.Configs;
import org.springframework.context.annotation.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
public class NetOplogAutoConfiguration {

    @Bean
    public OpLogSnapshotIniter opLogSnapshotIniter() {
        return new OpLogSnapshotIniter(Configs.getScanPaths());
    }

}
