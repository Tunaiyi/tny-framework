package com.tny.game.suite.cache.spring;

import com.tny.game.cache.mysql.DBCacheClient;
import com.tny.game.cache.mysql.DBItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("dbClient")
@Profile({"suite.cache", "suite.all"})
public class SpringDBCacheClient extends DBCacheClient {

    @Autowired
    public SpringDBCacheClient(DBItemFactory dbItemFactory) {
        super(dbItemFactory);
    }

}
