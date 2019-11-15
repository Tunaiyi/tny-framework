package com.tny.game.suite.cache.spring;

import com.tny.game.cache.*;
import com.tny.game.cache.mysql.*;
import com.tny.game.cache.redis.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("redisCache")
@Profile({CACHE_REDIS, CACHE_ALL})
public class SpringRedisDirectCache extends DirectCache {

    @Autowired
    SpringRedisDirectCache(BaseRedisCacheClient client,
            @Qualifier("dbItemFactory") RawCacheItemFactory<?, ? extends DBCacheItem<?>> cacheItemFactory,
            ToCacheClassHolderFactory toCCHolderFactory) {
        super(client, cacheItemFactory, toCCHolderFactory);
    }

}
