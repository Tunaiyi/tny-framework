package com.tny.game.suite.cache.spring;

import com.tny.game.cache.*;
import com.tny.game.cache.async.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("dbCacheSynchronizer")
@Profile({CACHE_DB, CACHE_ALL})
public class DBCacheSynchronizer extends CacheSynchronizer {

    @Autowired
    public DBCacheSynchronizer(@Qualifier("dbCache") DirectCache cameCache) {
        super(cameCache);
    }

}
