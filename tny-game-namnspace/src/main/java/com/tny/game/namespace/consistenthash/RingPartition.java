package com.tny.game.namespace.consistenthash;

import java.util.Objects;

/**
 * 分片分区
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 15:00
 **/
public class RingPartition<N extends ShardingNode> extends ShardingPartition<N> {

    private String key;

    private int index;

    private long slot = -1L;

    private N node;

    public RingPartition() {
    }

    public RingPartition(int index, N node) {
        this.key = node.getNodeId() + "$" + index;
        this.index = index;
        this.node = node;
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
    public void setSlot(long slot) {
        this.slot = slot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RingPartition)) {
            return false;
        }
        RingPartition<?> that = (RingPartition<?>)o;
        return getIndex() == that.getIndex() && getSlot() == that.getSlot() && Objects.equals(getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getIndex(), getSlot());
    }

}
