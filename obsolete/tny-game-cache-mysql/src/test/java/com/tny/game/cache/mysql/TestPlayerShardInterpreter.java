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
import com.tny.game.cache.testclass.*;
import org.springframework.core.annotation.Order;

@Order(-29)
public class TestPlayerShardInterpreter extends CacheShardInterpreter<TestPlayer> {

    protected TestPlayerShardInterpreter() {
        super(TestPlayer.class);
    }

    @Override
    protected String getTable(TestPlayer param) {
        return "CPlayer" + (param.getPlayerId() % 10);
    }

}
