/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.consistenthash.*;
import com.tny.game.namespace.consistenthash.listener.*;

import java.util.*;

/**
 * 一致性节点Hash环
 * <p>
 *
 * @author kgtny
 * @date 2022/7/7 04:04
 **/
public class EtcdNodeHashingRing<N extends ShardingNode> extends EtcdNodeHashing<N> implements NodeHashing<N> {

    // Hash 环
    private final ShardingSet<N> ring;

    protected EtcdNodeHashingRing(String rootPath, HashingOptions<N> option, NamespaceExplorer explorer,
            ObjectCodecAdapter objectCodecAdapter) {
        super(rootPath, option, explorer, objectCodecAdapter, true);
        this.ring = new ConsistentHashRing<>(getName(), option.getMaxSlots(), getKeyHasher());
        this.ring.event().add(new ShardingListener<>() {

            @Override
            public void onChange(Sharding<N> sharding, List<Partition<N>> partitions) {
                fireChange(sharding, partitions);
            }

            @Override
            public void onRemove(Sharding<N> sharding, List<Partition<N>> partitions) {
                fireRemove(sharding, partitions);
            }

        });
    }

    /**
     * 关闭
     */

    @Override
    protected void doShutdown() {
    }

    @Override
    public boolean contains(Partition<N> partition) {
        return ring.contains(partition);
    }

    @Override
    public List<Partition<N>> findPartitions(String nodeId) {
        return ring.findPartitions(nodeId);
    }

    @Override
    public List<ShardingRange<N>> findRanges(String nodeId) {
        return ring.findRanges(nodeId);
    }

    @Override
    public List<Partition<N>> getAllPartitions() {
        return ring.getAllPartitions();
    }

    @Override
    public List<ShardingRange<N>> getAllRanges() {
        return ring.getAllRanges();
    }

    @Override
    public Optional<Partition<N>> prevPartition(long slot) {
        return ring.prevPartition(slot);
    }

    @Override
    public Optional<Partition<N>> nextPartition(long slot) {
        return ring.nextPartition(slot);
    }

    @Override
    public Optional<Partition<N>> locate(String key) {
        return ring.locate(key);
    }

    @Override
    public List<Partition<N>> locate(String key, int count) {
        return ring.locate(key, count);
    }

    @Override
    public int partitionSize() {
        return ring.partitionSize();
    }

    @Override
    protected void loadPartitions(List<Partition<N>> partitions) {
        synchronized (this) {
            this.ring.clear();
            this.ring.addAll(partitions);
        }
    }

    @Override
    protected void putPartition(Partition<N> partition) {
        synchronized (this) {
            this.ring.save(partition);
        }
    }

    @Override
    protected void removePartition(Partition<N> partition) {
        synchronized (this) {
            this.ring.remove(partition.getSlotIndex());
        }
    }

}


