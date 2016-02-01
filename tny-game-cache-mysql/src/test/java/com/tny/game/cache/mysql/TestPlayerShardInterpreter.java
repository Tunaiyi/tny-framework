package com.tny.game.cache.mysql;

import com.tny.game.cache.shard.CacheShardInterpreter;
import com.tny.game.cache.testclass.TestPlayer;
import org.springframework.core.annotation.Order;

@Order(-29)
public class TestPlayerShardInterpreter extends CacheShardInterpreter<TestPlayer> {

    protected TestPlayerShardInterpreter() {
        super(TestPlayer.class);
    }

    @Override
    protected String getTable(TestPlayer param) {
        return "CPlayer" + (param.getPlayerID() % 10);
    }

}
