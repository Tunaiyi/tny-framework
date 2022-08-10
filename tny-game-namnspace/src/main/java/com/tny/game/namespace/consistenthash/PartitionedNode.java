package com.tny.game.namespace.consistenthash;

import com.tny.game.namespace.*;

import java.util.Objects;

/**
 * 分片分区
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 15:00
 **/
public class PartitionedNode<N extends ShardingNode> extends ShardingPartition<N> {
    
    private String key;

    private int index;

    private long slotIndex = -1L;

    private int seed = 0;

    private N node;

    public PartitionedNode() {
    }

    public PartitionedNode(int index, N node) {
        this.key = node.getKey() + "$" + index;
        this.index = index;
        this.node = node;
        this.seed = 0;
    }

    public PartitionedNode(int index, N node, long slotIndex) {
        this.key = node.getKey() + "$" + index;
        this.index = index;
        this.node = node;
        this.slotIndex = slotIndex;
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
    public long getSlotIndex() {
        return slotIndex;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "Partition[" + key + "] (" + slotIndex + ')';
    }

    @Override
    public void hash(Hasher<PartitionedNode<N>> hasher, long maxSlot) {
        this.seed++;
        this.slotIndex = Math.abs(hasher.hash(this, this.seed, maxSlot));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartitionedNode)) {
            return false;
        }
        PartitionedNode<?> that = (PartitionedNode<?>)o;
        return getIndex() == that.getIndex() && getSlotIndex() == that.getSlotIndex() && Objects.equals(getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getIndex(), getSlotIndex());
    }

}
