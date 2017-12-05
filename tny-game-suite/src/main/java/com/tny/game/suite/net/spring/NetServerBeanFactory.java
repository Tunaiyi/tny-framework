package com.tny.game.suite.net.spring;

import com.tny.game.common.word.LocalWordsFilter;
import com.tny.game.common.word.WordsFilter;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.executor.GroupBySessionDispatchCommandExecutor;
import com.tny.game.net.netty.NettyServer;
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
    public NettyServer nettyServer() {
        return new NettyServer(this.appConfiguration);
    }

}
