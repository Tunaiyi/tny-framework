package com.tny.game.suite.cache.spring;

import com.tny.game.asyndb.ReleaseStrategyFactory;
import com.tny.game.asyndb.SyncDBExecutor;
import com.tny.game.asyndb.SynchronizerHolder;
import com.tny.game.asyndb.impl.AverageRateBatchSyncDBExecutor;
import com.tny.game.asyndb.spring.SpringSynchronizerHolder;
import com.tny.game.cache.RawCacheItemFactory;
import com.tny.game.cache.shard.CacheKeyShardInterpreter;
import com.tny.game.common.config.Config;
import com.tny.game.suite.cache.DomainItemDBItemFactory;
import com.tny.game.suite.utils.Configs;
import net.paoding.rose.jade.shard.ShardInterpreter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@Profile({"suite.cache", "suite.cache.db", "suite.all"})
public class CacheConfiguration {


    @Bean
    public SynchronizerHolder synchronizerHolder() {
        return new SpringSynchronizerHolder();
    }

    @Bean
    public ShardInterpreter<String> cacheKeyShardInterpreter() {
        return new CacheKeyShardInterpreter();
    }

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
        return new OnlineReleaseStrategyFactory(
                Configs.SUITE_CONFIG.getLong(Configs.SUITE_ASYNC_OBJ_POOL_KEEP_TIME, Duration.ofMinutes(5).toMillis()));
    }

    @Bean
    public SpringToCacheClassHolderAndLinkHandlerFactory toCacheClassHolderAndLinkHandlerFactory() {
        return new SpringToCacheClassHolderAndLinkHandlerFactory(Configs.getScanPaths());
    }


}
