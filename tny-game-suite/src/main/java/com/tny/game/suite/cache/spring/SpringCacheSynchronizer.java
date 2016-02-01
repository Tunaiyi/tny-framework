package com.tny.game.suite.cache.spring;

import com.tny.game.cache.DirectCache;
import com.tny.game.cache.async.CacheSynchronizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("defaultCacheSynchronizer")
@Profile({"suite.cache", "suite.all"})
public class SpringCacheSynchronizer extends CacheSynchronizer {

    @Autowired
    public SpringCacheSynchronizer(DirectCache cameCache) {
        super(cameCache);
    }

}
