package com.tny.game.namespace.consistenthash;

import com.google.common.collect.Range;

import java.util.*;

/**
 * 分片分区范围
 * <p>
 *
 * @author kgtny
 * @date 2022/7/7 18:01
 **/
public class ShardingRange<N extends ShardingNode> {

    private final Partition<N> partition;

    private final long fromSlot;

    private final long toSlot;

    private final long maxSlot;

    private boolean across;

    private List<Range<Long>> ranges;

    public ShardingRange(Partition<N> prevPartition, Partition<N> partition, long maxSlot) {
        this.partition = partition;
        long fromSlot = prevPartition.getSlot() + 1;
        if (fromSlot > maxSlot) {
            fromSlot = 0;
        }
        this.maxSlot = maxSlot;
        this.fromSlot = fromSlot;
        this.toSlot = partition.getSlot();
        if (fromSlot > toSlot) {
            across = true;
        }
    }

    public Partition<N> getPartition() {
        return partition;
    }

    public List<Range<Long>> getRanges() {
        if (ranges != null) {
            return ranges;
        }
        if (across) {
            return ranges = Arrays.asList(
                    Range.closed(0L, this.toSlot),
                    Range.closed(this.fromSlot, this.maxSlot));
        } else {
            return ranges = Collections.singletonList(Range.closed(fromSlot, toSlot));
        }
    }

    public long getFromSlot() {
        return fromSlot;
    }

    public long getToSlot() {
        return toSlot;
    }

    public boolean isAcross() {
        return across;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(partition +
                "{" + fromSlot +
                " to " + toSlot +
                '}');
        getRanges().forEach(r -> sb.append('[').append(r.lowerEndpoint()).append('-').append(r.upperEndpoint()).append(']'));
        return sb.toString();
    }

}
