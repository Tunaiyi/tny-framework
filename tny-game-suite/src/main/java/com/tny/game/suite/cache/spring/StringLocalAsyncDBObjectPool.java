package com.tny.game.suite.cache.spring;

import com.tny.game.asyndb.LocalAsyncDBObjectPool;
import com.tny.game.asyndb.ReleaseStrategyFactory;
import com.tny.game.asyndb.SyncDBExecutor;
import com.tny.game.asyndb.SynchronizerHolder;
import com.tny.game.suite.utils.Configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("objectPool")
@Profile({"suite.cache", "suite.all"})
public class StringLocalAsyncDBObjectPool extends LocalAsyncDBObjectPool {

    @Autowired
    public StringLocalAsyncDBObjectPool(SyncDBExecutor syncDBExecutor, ReleaseStrategyFactory releaseStrategyFactory, SynchronizerHolder synchronizerHolder) {
        super(syncDBExecutor, releaseStrategyFactory, synchronizerHolder,
                Configs.SUITE_CONFIG.getInt(Configs.SUITE_ASYNC_OBJ_POOL_RECYCLE_TIME, 10000));
    }


}
