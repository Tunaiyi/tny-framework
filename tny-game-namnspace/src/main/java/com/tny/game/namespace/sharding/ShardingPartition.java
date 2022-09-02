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
package com.tny.game.namespace.sharding;

/**
 * 分区节点(虚拟节点)
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 14:40
 **/
public abstract class ShardingPartition<N extends ShardingNode> implements Partition<N> {

    /**
     * 重hash
     *
     * @param hasher   hash 器
     * @param maxSlots 最大槽数
     */
    public abstract void hash(Hasher<PartitionSlot<N>> hasher, long maxSlots);

}
