package com.tny.game.namespace;

import com.tny.game.common.type.*;
import com.tny.game.namespace.consistenthash.*;

/**
 * 一致性 hash 选项
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 03:10
 **/
public class HashingOptions<N extends ShardingNode> {

    private final String name;

    private final long ttl;

    private final HashAlgorithm algorithm;

    private final int partitionCount;

    private final ReferenceType<RingPartition<N>> type;

    private HashingOptions(Builder<N> builder) {
        name = builder.name;
        ttl = builder.ttl;
        algorithm = builder.algorithm;
        partitionCount = builder.partitionCount;
        type = builder.type;
    }

    public static <N extends ShardingNode> Builder<N> newBuilder(ReferenceType<RingPartition<N>> type) {
        return new Builder<N>().setType(type);
    }

    public static <N extends ShardingNode> Builder<N> newBuilder(HashingOptions<N> copy) {
        var builder = new Builder<N>();
        builder.name = copy.getName();
        builder.ttl = copy.getTtl();
        builder.algorithm = copy.getAlgorithm();
        builder.partitionCount = copy.getPartitionCount();
        builder.type = copy.getType();
        return builder;
    }

    public String getName() {
        return name;
    }

    public long getTtl() {
        return ttl;
    }

    public HashAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public ReferenceType<RingPartition<N>> getType() {
        return type;
    }

    public static final class Builder<N extends ShardingNode> {

        private String name;

        private long ttl = 3000;

        private HashAlgorithm algorithm = HashAlgorithms.getDefault();

        private int partitionCount = 5;

        private ReferenceType<RingPartition<N>> type;

        private Builder() {
        }

        public Builder<N> setName(String name) {
            this.name = name;
            return this;
        }

        public Builder<N> setTtl(long ttl) {
            this.ttl = ttl <= 1000 ? 5000 : ttl;
            return this;
        }

        public Builder<N> setAlgorithm(HashAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder<N> setPartitionCount(int partitionCount) {
            this.partitionCount = partitionCount;
            return this;
        }

        public Builder<N> setType(ReferenceType<RingPartition<N>> type) {
            this.type = type;
            return this;
        }

        public HashingOptions<N> build() {
            return new HashingOptions<>(this);
        }

    }

}
