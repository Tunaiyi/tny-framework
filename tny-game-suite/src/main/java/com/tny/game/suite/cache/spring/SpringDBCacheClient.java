package com.tny.game.suite.cache.spring;

import com.tny.game.cache.RawCacheItemFactory;
import com.tny.game.cache.mysql.DBCacheClient;
import com.tny.game.cache.mysql.DBCacheItem;
import com.tny.game.cache.mysql.dao.CacheDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("dbClient")
@Profile({"suite.cache", "suite.cache.db", "suite.all"})
public class SpringDBCacheClient extends DBCacheClient {

    @Autowired
    public SpringDBCacheClient(CacheDAO dao,
                               @Qualifier("dbItemFactory") RawCacheItemFactory<?, ? extends DBCacheItem<?>> cacheItemFactory) {
        super(dao, cacheItemFactory);
    }

}
