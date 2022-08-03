package com.tny.game.namespace;

import com.tny.game.common.type.*;
import com.tny.game.namespace.consistenthash.*;

import java.nio.charset.Charset;

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

    private final Hasher<String> keyHasher;

    private final Hasher<N> nodeHasher;

    private final int partitionCount;

    private final ReferenceType<NodePartition<N>> type;

    private final Charset charset;

    private HashingOptions(Builder<N> builder) {
        name = builder.name;
        ttl = builder.ttl;
        keyHasher = builder.keyHasher;
        nodeHasher = builder.nodeHasher;
        charset = builder.charset;
        partitionCount = builder.partitionCount;
        type = builder.type;
    }

    public static <N extends ShardingNode> Builder<N> newBuilder(
            ReferenceType<NodePartition<N>> type, Hasher<String> keyHasher, Hasher<N> nodeHasher) {
        return new Builder<N>().setType(type).setKeyHasher(keyHasher).setNodeHasher(nodeHasher);
    }

    public static <N extends ShardingNode> Builder<N> newBuilder(HashingOptions<N> copy) {
        var builder = new Builder<N>();
        builder.name = copy.getName();
        builder.ttl = copy.getTtl();
        builder.charset = copy.getCharset();
        builder.keyHasher = copy.getKeyHasher();
        builder.nodeHasher = copy.getNodeHasher();
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

    public Hasher<String> getKeyHasher() {
        return keyHasher;
    }

    public Hasher<N> getNodeHasher() {
        return nodeHasher;
    }

    public Charset getCharset() {
        return charset;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public ReferenceType<NodePartition<N>> getType() {
        return type;
    }

    public static final class Builder<N extends ShardingNode> {

        private String name;

        private long ttl = 3000;

        private Hasher<String> keyHasher;

        private Hasher<N> nodeHasher;

        private Charset charset = NamespaceConstants.CHARSET;

        private int partitionCount = 5;

        private ReferenceType<NodePartition<N>> type;

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

        public Builder<N> setKeyHasher(Hasher<String> keyHasher) {
            this.keyHasher = keyHasher;
            return this;
        }

        public Builder<N> setNodeHasher(Hasher<N> nodeHasher) {
            this.nodeHasher = nodeHasher;
            return this;
        }

        public Builder<N> setCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder<N> setPartitionCount(int partitionCount) {
            this.partitionCount = partitionCount;
            return this;
        }

        public Builder<N> setType(ReferenceType<NodePartition<N>> type) {
            this.type = type;
            return this;
        }

        public HashingOptions<N> build() {
            return new HashingOptions<>(this);
        }

    }

}
