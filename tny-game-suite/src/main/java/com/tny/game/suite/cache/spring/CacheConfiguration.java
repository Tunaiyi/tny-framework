package com.tny.game.suite.cache.spring;

import com.tny.game.asyndb.*;
import com.tny.game.asyndb.impl.*;
import com.tny.game.asyndb.spring.*;
import com.tny.game.cache.*;
import com.tny.game.common.io.config.*;
import com.tny.game.net.command.*;
import com.tny.game.suite.cache.*;
import com.tny.game.suite.utils.*;
import org.springframework.context.annotation.*;

import java.time.Duration;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@Profile({CACHE_ASYNC, CACHE_DB, CACHE_REDIS, CACHE_ALL})
public class CacheConfiguration {

    @Bean
    public SynchronizerHolder synchronizerHolder() {
        return new SpringSynchronizerHolder();
    }

    // @Bean
    // public ShardInterpreter<String> cacheKeyShardInterpreter() {
    //     return new CacheKeyShardInterpreter();
    // }

    @Bean(name = "dbItemFactory")
    public RawCacheItemFactory dbItemFactory() {
        return new DomainItemDBItemFactory();
    }

    @Bean
    public SyncDBExecutor syncDBExecutor() {
        Config config = Configs.SUITE_CONFIG;
        return new AverageRateBatchSyncDBExecutor(
                config.getInt(Configs.SUITE_ASYNC_DB_EXE_STEP, 3000),
                config.getLong(Configs.SUITE_ASYNC_DB_EXE_WAIT_TIME, 2000),
                config.getInt(Configs.SUITE_ASYNC_DB_EXE_TRY_TIME, 3),
                config.getInt(Configs.SUITE_ASYNC_DB_EXE_THREAD_SIZE, 50));
    }

    @Bean
    public ReleaseStrategyFactory releaseStrategyFactory() {
        return new OnlineReleaseStrategyFactory(Certificates.DEFAULT_USER_TYPE,
                Configs.SUITE_CONFIG.getLong(Configs.SUITE_ASYNC_OBJ_POOL_KEEP_TIME, Duration.ofMinutes(5).toMillis()));
    }

    @Bean
    public SpringToCacheClassHolderAndLinkHandlerFactory toCacheClassHolderAndLinkHandlerFactory() {
        return new SpringToCacheClassHolderAndLinkHandlerFactory(Configs.getScanPathList());
    }

}
