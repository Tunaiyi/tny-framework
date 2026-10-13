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
