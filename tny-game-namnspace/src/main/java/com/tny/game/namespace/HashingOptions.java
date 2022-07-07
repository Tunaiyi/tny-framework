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

    private String name;

    private long ttl = 3000;

    private HashAlgorithm algorithm = HashAlgorithms.XX3_32_HASH;

    private int partitionCount = 5;

    private ReferenceType<ShardingPartition<N>> type;

    public static <N extends ShardingNode> HashingOptions<N> option(ReferenceType<ShardingPartition<N>> type) {
        return new HashingOptions<N>()
                .withType(type);
    }

    public HashingOptions<N> setTtl(long ttl) {
        this.ttl = ttl <= 1000 ? 5000 : ttl;
        return this;
    }

    public HashingOptions<N> withName(String name) {
        this.name = name;
        return this;
    }

    public HashingOptions<N> withAlgorithm(HashAlgorithm algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public HashingOptions<N> withPartitionCount(int partitionCount) {
        this.partitionCount = partitionCount;
        return this;
    }

    public HashingOptions<N> withType(ReferenceType<ShardingPartition<N>> type) {
        this.type = type;
        return this;
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

    public ReferenceType<ShardingPartition<N>> getType() {
        return type;
    }

}
