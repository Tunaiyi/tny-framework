/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.namespace.consistenthash.listener;

import com.tny.game.namespace.consistenthash.*;

import java.util.List;

/**
 * 分片监听器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 02:46
 **/
public interface ShardingListener<N extends ShardingNode> {

    /**
     * 增加分片改变
     *
     * @param sharding 改变分片
     */
    void onChange(Sharding<N> sharding, List<Partition<N>> partitions);

    /**
     * 增加分片改变
     *
     * @param sharding 改变分片
     */
    void onRemove(Sharding<N> sharding, List<Partition<N>> partitions);

}
