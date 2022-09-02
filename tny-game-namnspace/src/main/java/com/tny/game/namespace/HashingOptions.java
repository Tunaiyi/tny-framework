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
package com.tny.game.namespace;

import com.tny.game.common.type.*;
import com.tny.game.namespace.sharding.*;

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

    private final Hasher<PartitionSlot<N>> nodeHasher;

    private final int partitionCount;

    private final long maxSlots;

    private final ReferenceType<PartitionSlot<N>> type;

    private final Charset charset;

    private HashingOptions(Builder<N> builder) {
        name = builder.name;
        ttl = builder.ttl;
        keyHasher = builder.keyHasher;
        nodeHasher = builder.nodeHasher;
        charset = builder.charset;
        partitionCount = builder.partitionCount;
        maxSlots = builder.maxSlots;
        type = builder.type;
    }

    public static <N extends ShardingNode> Builder<N> newBuilder(
            ReferenceType<PartitionSlot<N>> type, long maxSlotSize, Hasher<String> keyHasher, Hasher<PartitionSlot<N>> nodeHasher) {
        return new Builder<N>().setMaxSlots(maxSlotSize).setType(type).setKeyHasher(keyHasher).setNodeHasher(nodeHasher);
    }

    public static <N extends ShardingNode> Builder<N> newBuilder(HashingOptions<N> copy) {
        var builder = new Builder<N>();
        builder.name = copy.getName();
        builder.ttl = copy.getTtl();
        builder.charset = copy.getCharset();
        builder.keyHasher = copy.getKeyHasher();
        builder.nodeHasher = copy.getNodeHasher();
        builder.maxSlots = copy.getMaxSlots();
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

    public long getMaxSlots() {
        return maxSlots;
    }

    public Hasher<String> getKeyHasher() {
        return keyHasher;
    }

    public Hasher<PartitionSlot<N>> getNodeHasher() {
        return nodeHasher;
    }

    public Charset getCharset() {
        return charset;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public ReferenceType<PartitionSlot<N>> getType() {
        return type;
    }

    public static final class Builder<N extends ShardingNode> {

        private String name;

        private long ttl = 3000;

        private Hasher<String> keyHasher;

        private Hasher<PartitionSlot<N>> nodeHasher;

        private Charset charset = NamespaceConstants.CHARSET;

        private int partitionCount = 5;

        private long maxSlots = 1024;

        private ReferenceType<PartitionSlot<N>> type;

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

        public Builder<N> setNodeHasher(Hasher<PartitionSlot<N>> nodeHasher) {
            this.nodeHasher = nodeHasher;
            return this;
        }

        public Builder<N> setCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder<N> setMaxSlots(long maxSlots) {
            this.maxSlots = maxSlots;
            return this;
        }

        public Builder<N> setPartitionCount(int partitionCount) {
            this.partitionCount = partitionCount;
            return this;
        }

        public Builder<N> setType(ReferenceType<PartitionSlot<N>> type) {
            this.type = type;
            return this;
        }

        public HashingOptions<N> build() {
            return new HashingOptions<>(this);
        }

    }

}
