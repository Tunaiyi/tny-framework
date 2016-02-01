package com.tny.game.cache.mysql;

import com.tny.game.cache.shard.CacheShardInterpreter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

@Order(-30)
public class TestShardInterpreter extends CacheShardInterpreter<String> {

    protected TestShardInterpreter() {
        super(String.class);
    }

    @Override
    protected String getTable(String param) {
        String key = param.toString();
        if (key.startsWith("CPlayer")) {
            int value = Integer.parseInt(param.toString().substring("CPlayer".length(), param.toString().length()));
            return "CPlayer" + (value % 10);
        }
        if (key.startsWith("player")) {
            int value = Integer.parseInt(StringUtils.split(key, ":")[1]);
            return "CPlayer" + (value % 10);
        }
        return "CPlayer" + (Math.abs(param.hashCode()) % 10);
    }

}
