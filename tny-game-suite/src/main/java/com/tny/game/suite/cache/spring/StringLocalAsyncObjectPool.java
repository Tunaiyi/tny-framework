package com.tny.game.suite.cache.spring;

import com.tny.game.asyndb.*;
import com.tny.game.suite.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("objectPool")
@Profile({CACHE_ASYNC, CACHE_ALL})
public class StringLocalAsyncObjectPool extends LocalAsyncObjectPool {

    @Autowired
    public StringLocalAsyncObjectPool(SyncDBExecutor syncDBExecutor, ReleaseStrategyFactory releaseStrategyFactory,
            SynchronizerHolder synchronizerHolder) {
        super(syncDBExecutor, releaseStrategyFactory, synchronizerHolder,
                Configs.SUITE_CONFIG.getInt(Configs.SUITE_ASYNC_OBJ_POOL_RECYCLE_TIME, 10000));
    }

}
