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
package com.tny.game.basics.item;

import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.concurrent.CompletionStage;

/**
 * 可获取的管理器
 *
 * @param <O>
 * @author KGTny
 */
public abstract class AsyncQueryManager<O> implements AsyncManager<O> {

    /**
     * 获取玩家的对象
     *
     * @param anyId 对象 id
     * @return 返回对象
     */
    protected abstract CompletionStage<O> get(AnyId anyId);

    /**
     * 批量获取玩家的对象
     *
     * @param anyIdList 对象 id 列表
     * @return 返回对象
     */
    protected abstract CompletionStage<List<O>> get(Collection<AnyId> anyIdList);

    /**
     * 获取玩家的对象
     *
     * @param playerId 玩家id
     * @param id       item的id
     * @return 返回对象
     */
    protected abstract CompletionStage<O> get(long playerId, long id);

    /**
     * 获取玩家当前类型的所有对象
     *
     * @param playerId 玩家id
     * @return 返回对象
     */
    protected CompletionStage<List<O>> find(long playerId) {
        return find(ImmutableMap.of("playerId", playerId));
    }

    /**
     * 按索引字段查找
     *
     * @param query 索引调节
     * @return 返回查找信息
     */
    protected abstract CompletionStage<List<O>> find(Map<String, Object> query);

    /**
     * 查找所有
     *
     * @return 返回查找信息
     */
    protected abstract CompletionStage<List<O>> findAll();

}
