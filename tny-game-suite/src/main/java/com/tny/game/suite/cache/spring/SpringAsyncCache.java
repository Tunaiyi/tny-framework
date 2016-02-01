package com.tny.game.suite.cache.spring;

import com.tny.game.asyndb.DBObjectPool;
import com.tny.game.cache.ToCacheClassHolderFactory;
import com.tny.game.cache.async.AsyncCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("asyncCache")
@Profile({"suite.cache", "suite.all"})
public class SpringAsyncCache extends AsyncCache {

    @Autowired
    public SpringAsyncCache(ToCacheClassHolderFactory toCacheClassHolderFactory, DBObjectPool pool) {
        super(toCacheClassHolderFactory, pool);
    }

}
