package com.tny.game.suite.cache.spring;

import com.tny.game.cache.AlterCacheItemFactory;
import com.tny.game.cache.CacheClient;
import com.tny.game.cache.DirectCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("directCache")
@Profile({"suite.cache", "suite.all"})
public class SpringDirectCache extends DirectCache {

    @Autowired
    public SpringDirectCache(CacheClient client, AlterCacheItemFactory cacheItemFactory) {
        super(client, cacheItemFactory);
    }

}
