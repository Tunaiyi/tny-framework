package com.tny.game.suite.cache.spring;

import com.tny.game.cache.*;
import com.tny.game.cache.mysql.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("dbCache")
@Profile({CACHE_DB, CACHE_ALL})
public class SpringDBDirectCache extends DirectCache {

    @Autowired
    SpringDBDirectCache(DBCacheClient client,
            @Qualifier("dbItemFactory") RawCacheItemFactory<?, ? extends DBCacheItem<?>> cacheItemFactory,
            ToCacheClassHolderFactory toCCHolderFactory) {
        super(client, cacheItemFactory, toCCHolderFactory);
    }

}
