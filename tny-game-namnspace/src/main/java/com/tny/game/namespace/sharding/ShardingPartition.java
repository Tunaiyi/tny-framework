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
