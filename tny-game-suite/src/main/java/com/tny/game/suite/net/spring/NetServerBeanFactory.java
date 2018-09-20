package com.tny.game.suite.net.spring;

import com.tny.game.common.word.*;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.executor.GroupBySessionDispatchCommandExecutor;
import com.tny.game.net.netty.NettyBinder;
import com.tny.game.suite.utils.Configs;
import org.springframework.context.annotation.*;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@Profile({GAME})
public class NetServerBeanFactory extends NetBaseBeanFactory {

    @Bean(name = "dispatchCommandExecutor")
    public DispatchCommandExecutor getDispatchCommandExecutor() {
        return new GroupBySessionDispatchCommandExecutor(this.appConfiguration.getProperties());
    }

    @Bean
    public WordsFilter wordsFilter() throws Exception {
        LocalWordsFilter filter = new LocalWordsFilter(
                Configs.SUITE_CONFIG.getStr(Configs.SUITE_WORD_FILTER_CONFIG_PATH, Configs.WORD_FILTER_CONFIG_PATH),
                Configs.SUITE_CONFIG.getStr(Configs.SUITE_WORD_REPLACE_SYMBOL, "*"));
        filter.load();
        return filter;
    }

    @Bean
    @Profile({SERVER, GAME})
    public NettyBinder nettyServer() {
        return new NettyBinder(new ReadTimeoutChannelMaker<>(60000 * 3), this.appConfiguration);
    }

}
