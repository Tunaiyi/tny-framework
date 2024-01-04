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

import java.util.Objects;

/**
 * 分片分区
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 15:00
 **/
public class PartitionSlot<N extends ShardingNode> extends ShardingPartition<N> {

    private String key;

    private int index;

    private long slot = -1L;

    private int seed = 0;

    private N node;

    public PartitionSlot() {
    }

    public PartitionSlot(int index, N node) {
        this.key = node.getKey() + "$" + index;
        this.index = index;
        this.node = node;
        this.seed = 0;
    }

    public PartitionSlot(int index, N node, long slot) {
        this.key = node.getKey() + "$" + index;
        this.index = index;
        this.node = node;
        this.slot = slot;
        this.seed = 0;
    }

    @Override
    public N getNode() {
        return node;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public long getSlot() {
        return slot;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "Partition[" + key + "] (" + slot + ')';
    }

    @Override
    public void hash(Hasher<PartitionSlot<N>> hasher, long maxSlots) {
        this.seed++;
        this.slot = Math.abs(hasher.hash(this, this.seed, maxSlots));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartitionSlot)) {
            return false;
        }
        PartitionSlot<?> that = (PartitionSlot<?>) o;
        return getIndex() == that.getIndex() && Objects.equals(getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getIndex());
    }

}
