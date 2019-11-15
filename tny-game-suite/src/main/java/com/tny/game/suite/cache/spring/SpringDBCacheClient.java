package com.tny.game.suite.cache.spring;

import com.tny.game.cache.*;
import com.tny.game.cache.mysql.*;
import com.tny.game.cache.mysql.dao.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("dbClient")
@Profile({CACHE_DB, CACHE_ALL})
public class SpringDBCacheClient extends DBCacheClient {

    @Autowired
    public SpringDBCacheClient(CacheDAO dao,
            @Qualifier("dbItemFactory") RawCacheItemFactory<?, ? extends DBCacheItem<?>> cacheItemFactory) {
        super(dao, cacheItemFactory);
    }

}
