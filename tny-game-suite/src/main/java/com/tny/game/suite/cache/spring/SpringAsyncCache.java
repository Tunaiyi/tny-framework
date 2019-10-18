package com.tny.game.suite.cache.spring;

import com.tny.game.asyndb.ObjectPool;
import com.tny.game.cache.ToCacheClassHolderFactory;
import com.tny.game.cache.async.AsyncCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("asyncCache")
@Profile({CACHE_ASYNC, CACHE_ALL})
public class SpringAsyncCache extends AsyncCache {

    @Autowired
    public SpringAsyncCache(ToCacheClassHolderFactory toCacheClassHolderFactory, ObjectPool pool) {
        super(toCacheClassHolderFactory, pool);
    }

}
