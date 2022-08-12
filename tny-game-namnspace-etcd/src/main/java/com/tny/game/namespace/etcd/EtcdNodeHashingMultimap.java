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

import com.google.common.collect.*;
import com.tny.game.codec.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.consistenthash.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.stream.Collectors;

/**
 * 多节点 Hash 集群(每个哈希槽可以多个节点)
 * <p>
 *
 * @author kgtny
 * @date 2022/7/7 04:04
 **/
public class EtcdNodeHashingMultimap<N extends ShardingNode> extends EtcdNodeHashing<N> {

    // 节点
    private final Multimap<Long, Partition<N>> partitionMap = Multimaps.newSetMultimap(new ConcurrentHashMap<>(), CopyOnWriteArraySet::new);

    private final ReadWriteLock mutex = new ReentrantReadWriteLock(true);

    private volatile List<ShardingRange<N>> ranges;

    protected EtcdNodeHashingMultimap(String rootPath, HashingOptions<N> option, NamespaceExplorer explorer,
            ObjectCodecAdapter objectCodecAdapter) {
        super(rootPath, option, explorer, objectCodecAdapter, false);
    }

    @Override
    protected void loadPartitions(List<Partition<N>> partitions) {
        mutex.writeLock().lock();
        try {
            var addList = partitions.stream()
                    .filter(partition -> partitionMap.put(partition.getSlotIndex(), partition))
                    .collect(Collectors.toList());
            this.resetRange();
            this.fireChange(this, addList);
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    protected void putPartition(Partition<N> partition) {
        mutex.writeLock().lock();
        try {
            partitionMap.put(partition.getSlotIndex(), partition);
            this.resetRange();
            this.fireChange(this, Collections.singletonList(partition));
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    protected void removePartition(Partition<N> partition) {
        mutex.writeLock().lock();
        try {
            if (partitionMap.remove(partition.getSlotIndex(), partition)) {
                this.fireRemove(this, Collections.singletonList(partition));
                this.resetRange();
            }
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    protected void doShutdown() {
    }

    @Override
    public boolean contains(Partition<N> partition) {
        mutex.readLock().lock();
        try {
            return partitionMap.containsValue(partition);
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public List<Partition<N>> findPartitions(String nodeId) {
        mutex.readLock().lock();
        try {
            return partitionMap.values()
                    .stream()
                    .filter(p -> Objects.equals(nodeId, p.getNodeKey()))
                    .collect(Collectors.toList());
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public List<ShardingRange<N>> findRanges(String nodeId) {
        mutex.readLock().lock();
        try {
            return partitionMap.values()
                    .stream()
                    .filter(p -> Objects.equals(nodeId, p.getNodeKey()))
                    .map(p -> new ShardingRange<>(p.getSlotIndex(), p.getSlotIndex(), p, getMaxSlots()))
                    .collect(Collectors.toList());
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public List<ShardingRange<N>> getAllRanges() {
        mutex.readLock().lock();
        try {
            if (this.ranges != null) {
                return ranges;
            }
            this.ranges = partitionMap.values()
                    .stream()
                    .map(p -> new ShardingRange<>(p.getSlotIndex(), p.getSlotIndex(), p, getMaxSlots()))
                    .collect(Collectors.toList());
            return this.ranges;
        } finally {
            mutex.readLock().unlock();
        }
    }

    private void resetRange() {
        this.ranges = null;
    }

    @Override
    public Optional<Partition<N>> prevPartition(long slot) {
        return locateBySlot(slot);
    }

    @Override
    public Optional<Partition<N>> nextPartition(long slot) {
        return locateBySlot(slot);
    }

    private Optional<Partition<N>> locateBySlot(long slot) {
        mutex.readLock().lock();
        try {
            var index = slot % getMaxSlots();
            return partitionMap.get(index).stream().findFirst();
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public List<Partition<N>> getAllPartitions() {
        mutex.readLock().lock();
        try {
            return new ArrayList<>(partitionMap.values());
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public Optional<Partition<N>> locate(String key) {
        mutex.readLock().lock();
        try {
            return locateBySlot(keyHash(key));
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public List<Partition<N>> locate(String key, int count) {
        mutex.readLock().lock();
        try {
            mutex.readLock().lock();
            try {
                return partitionMap.get(keyHash(key)).stream().limit(count).collect(Collectors.toList());
            } finally {
                mutex.readLock().unlock();
            }
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    protected String partitionedNodePath(String slotPath, PartitionedNode<N> partition) {
        return NamespacePathNames.nodePath(slotPath, partition.getKey());
    }

    @Override
    public int partitionSize() {
        return partitionMap.size();
    }

}


