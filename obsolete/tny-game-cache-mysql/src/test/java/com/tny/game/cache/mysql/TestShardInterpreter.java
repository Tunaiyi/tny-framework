/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.cache.mysql;

import com.tny.game.cache.shard.*;
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
