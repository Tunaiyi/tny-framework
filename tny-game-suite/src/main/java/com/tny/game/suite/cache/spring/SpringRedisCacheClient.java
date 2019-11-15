package com.tny.game.suite.cache.spring;

import com.tny.game.cache.redis.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import java.io.IOException;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
@Component("redisClient")
@Profile({CACHE_REDIS, CACHE_ALL})
public class SpringRedisCacheClient extends RedisCacheClient {

    @Autowired
    public SpringRedisCacheClient(JedisPool pool) throws IOException {
        super(pool);
    }

}
