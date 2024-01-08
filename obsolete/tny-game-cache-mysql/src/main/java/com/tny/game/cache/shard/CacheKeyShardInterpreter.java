package com.tny.game.cache.shard;

import org.springframework.core.annotation.Order;

@Order(-20)
public class CacheKeyShardInterpreter extends CacheShardInterpreter<String> {

    public CacheKeyShardInterpreter() {
        super(String.class);
    }

}