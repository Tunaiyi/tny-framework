package com.tny.game.suite.net.spring;

import com.tny.game.common.config.Config;
import com.tny.game.common.word.LocalWordsFilter;
import com.tny.game.common.word.WordsFilter;
import com.tny.game.net.checker.md5.MD5RequestChecker;
import com.tny.game.net.config.ServerConfigFactory;
import com.tny.game.net.config.properties.PropertiesServerConfigFactory;
import com.tny.game.net.dispatcher.ServerSessionFactory;
import com.tny.game.net.dispatcher.SessionHolder;
import com.tny.game.net.dispatcher.plugin.PluginHolder;
import com.tny.game.net.dispatcher.plugin.spring.SpringPluginHolder;
import com.tny.game.net.dispatcher.session.mobile.MobileSessionFactory;
import com.tny.game.net.dispatcher.session.mobile.MobileSessionHolder;
import com.tny.game.net.dispatcher.spring.SpringMessageDispatcher;
import com.tny.game.net.executor.normal.ThreadPoolCommandExecutor;
import com.tny.game.suite.login.GameMD5Checker;
import com.tny.game.suite.utils.Configs;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@Profile({"suite.server", "suite.all"})
public class ServerConfiguration {

    @Bean(name = "commandExecutor")
    public ThreadPoolCommandExecutor threadPoolCommandExecutor() {
        Config config = Configs.SUITE_CONFIG;
        int corePoolSize = config.getInt(Configs.SUITE_EXECUTOR_THREAD_SIZE, Runtime.getRuntime().availableProcessors() * 2);
        int maxPoolSize = config.getInt(Configs.SUITE_EXECUTOR_THREAD_MAX_SIZE, Runtime.getRuntime().availableProcessors() * 2);
        long keepAliveTime = config.getLong(Configs.SUITE_EXECUTOR_KEEP_ALIVE_TIME, Duration.ofMinutes(15).toMillis());
        return new ThreadPoolCommandExecutor(corePoolSize, maxPoolSize, keepAliveTime);
    }

    @Bean(name = "serverConfigFactory")
    public ServerConfigFactory serverConfigFactory() {
        return new PropertiesServerConfigFactory(Configs.SERVICE_CONFIG_PATH);
    }

    @Bean(name = "sessionFactory")
    public ServerSessionFactory serverSessionFactory() {
        return new MobileSessionFactory();
    }

    @Bean(name = "checker")
    public MD5RequestChecker requestChecker() {
        Config config = Configs.SUITE_CONFIG;
        String protsWords = config.getStr(Configs.SUITE_REQ_CHECKER_DIRECT_PROTS);
        String randomWords = config.getStr(Configs.SUITE_REQ_CHECKER_RANDOM_SEQ);
        List<Integer> ports = new ArrayList<>();
        if (protsWords != null) {
            ports = Arrays.asList(StringUtils.split(protsWords, ","))
                    .stream()
                    .map(NumberUtils::toInt)
                    .collect(Collectors.toList());
        }
        short[] randomKey = new short[0];
        if (randomWords != null) {
            randomKey = ArrayUtils.toPrimitive(
                    Arrays.asList(StringUtils.split(randomWords, ","))
                            .stream()
                            .map(NumberUtils::toShort)
                            .collect(Collectors.toList())
                            .toArray(new Short[0]));
        }
        return new GameMD5Checker(ports, randomKey);
    }

    @Bean(name = "messageDispatcher")
    public SpringMessageDispatcher messageDispatcher() {
        return new SpringMessageDispatcher(true);
    }

    @Bean(name = "sessionHolder")
    public SessionHolder serverSessionHolder() {
        long waitTime = Configs.SUITE_CONFIG.getLong(Configs.SUITE_SESSION_OFFLINE_WAIT, 60000);
        int cacheSize = Configs.SUITE_CONFIG.getInt(Configs.SUITE_SESSION_CACHE_RESP_SIZE, 5);
        MobileSessionHolder sessionHolder = new MobileSessionHolder();
        sessionHolder.setResponseCacheSize(cacheSize);
        sessionHolder.setOfflineWait(waitTime);
        return sessionHolder;
    }

    @Bean(name = "pluginHolder")
    public PluginHolder pluginHolder() {
        return new SpringPluginHolder();
    }

    @Bean
    public WordsFilter wordsFilter() {
        return new LocalWordsFilter(
                Configs.SUITE_CONFIG.getStr(Configs.SUITE_WORD_FILTER_CONFIG_PATH, Configs.WORD_FILTER_CONFIG_PATH),
                Configs.SUITE_CONFIG.getStr(Configs.SUITE_WORD_REPLACE_SYMBOL, "*"));
    }

}
