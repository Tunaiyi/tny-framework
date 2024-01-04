/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import com.google.common.collect.ImmutableMap;

import java.util.*;

/**
 * 可获取的管理器
 *
 * @param <O>
 * @author KGTny
 */
public abstract class QueryManager<O> implements Manager<O> {

    /**
     * 获取玩家的对象
     *
     * @param anyId 对象 id
     * @return 返回对象
     */
    protected abstract O get(AnyId anyId);

    /**
     * 批量获取玩家的对象
     *
     * @param anyIdList 对象 id 列表
     * @return 返回对象
     */
    protected abstract List<O> get(Collection<AnyId> anyIdList);

    /**
     * 获取玩家的对象
     *
     * @param playerId 玩家id
     * @param id       item的id
     * @return 返回对象
     */
    protected abstract O get(long playerId, long id);

    /**
     * 获取玩家当前类型的所有对象
     *
     * @param playerId 玩家id
     * @return 返回对象
     */
    protected List<O> find(long playerId) {
        return find(ImmutableMap.of("playerId", playerId));
    }

    /**
     * 按索引字段查找
     *
     * @param query 索引调节
     * @return 返回查找信息
     */
    protected abstract List<O> find(Map<String, Object> query);

    /**
     * 查找所有
     *
     * @return 返回查找信息
     */
    protected abstract List<O> findAll();

}
