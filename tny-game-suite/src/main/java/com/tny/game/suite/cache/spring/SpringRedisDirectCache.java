package com.tny.game.suite.cache.spring;

import com.tny.game.cache.DirectCache;
import com.tny.game.cache.RawCacheItemFactory;
import com.tny.game.cache.ToCacheClassHolderFactory;
import com.tny.game.cache.mysql.DBCacheItem;
import com.tny.game.cache.redis.RedisCacheClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("redisCache")
@Profile({"suite.cache", "suite.cache.redis", "suite.all"})
public class SpringRedisDirectCache extends DirectCache {

    @Autowired
    SpringRedisDirectCache(RedisCacheClient client,
                           @Qualifier("dbItemFactory") RawCacheItemFactory<?, ? extends DBCacheItem<?>> cacheItemFactory,
                           ToCacheClassHolderFactory toCCHolderFactory) {
        super(client, cacheItemFactory, toCCHolderFactory);
    }

}
