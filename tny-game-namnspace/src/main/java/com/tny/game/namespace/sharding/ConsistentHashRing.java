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
package com.tny.game.namespace.sharding;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.event.*;
import com.tny.game.common.notifier.*;
import com.tny.game.namespace.sharding.listener.*;
import org.slf4j.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.*;
import java.util.stream.*;

/**
 * 一致性哈希环
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 16:21
 **/
public class ConsistentHashRing<N extends ShardingNode> implements ShardingSet<N> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsistentHashRing.class);

    private final String name;

    private final ReadWriteLock mutex = new ReentrantReadWriteLock(true);

    private final NavigableMap<Long, Partition<N>> ring = new TreeMap<>();

    private final EventNotifier<ShardingListener<N>, Sharding<N>> notifier = EventNotifiers.notifier(ShardingListener.class);

    private List<ShardingRange<N>> ranges = ImmutableList.of();

    private final Hasher<String> hasher;

    private final long maxSlots;

    /**
     * 构造方法
     *
     * @param hasher hash算法
     */
    public ConsistentHashRing(String name, long maxSlots, Hasher<String> hasher) {
        this.name = name;
        this.maxSlots = maxSlots;
        this.hasher = hasher;
    }

    @Override
    public boolean add(Partition<N> partition) {
        mutex.writeLock().lock();
        try {
            if (ring.putIfAbsent(partition.getSlot(), partition) == null) {
                this.resetRanges();
                return true;
            }
        } finally {
            mutex.writeLock().unlock();
        }
        return false;
    }

    @Override
    public List<Partition<N>> addAll(Collection<Partition<N>> partitions) {
        mutex.writeLock().lock();
        try {
            List<Partition<N>> addList = partitions.stream()
                    .filter(partition -> ring.putIfAbsent(partition.getSlot(), partition) == null)
                    .collect(Collectors.toList());
            if (!addList.isEmpty()) {
                this.resetRanges();
                notifier.notify(ShardingListener::onChange, this, addList);
            }
            return addList;
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    public boolean update(Partition<N> partition) {
        mutex.writeLock().lock();
        try {
            if (ring.containsKey(partition.getSlot())) {
                ring.put(partition.getSlot(), partition);
                resetRanges();
                notifier.notify(ShardingListener::onChange, this, Collections.singletonList(partition));
                return true;
            }
        } finally {
            mutex.writeLock().unlock();
        }
        return false;
    }

    @Override
    public void save(Partition<N> partition) {
        mutex.writeLock().lock();
        try {
            ring.put(partition.getSlot(), partition);
            resetRanges();
            notifier.notify(ShardingListener::onChange, this, Collections.singletonList(partition));
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    public Partition<N> remove(long slot) {
        mutex.writeLock().lock();
        try {
            Partition<N> partition = ring.remove(slot);
            if (partition != null) {
                this.resetRanges();
                notifier.notify(ShardingListener::onRemove, this, Collections.singletonList(partition));
            }
            return partition;
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    public boolean remove(Partition<N> partition) {
        mutex.writeLock().lock();
        try {
            if (ring.remove(partition.getSlot(), partition)) {
                this.resetRanges();
                notifier.notify(ShardingListener::onRemove, this, Collections.singletonList(partition));
                return true;
            }
        } finally {
            mutex.writeLock().unlock();
        }
        return false;
    }

    @Override
    public void clear() {
        mutex.writeLock().lock();
        try {
            if (!ring.isEmpty()) {
                List<Partition<N>> clearList = new ArrayList<>(ring.values());
                ring.clear();
                this.resetRanges();
                notifier.notify(ShardingListener::onRemove, this, clearList);
            }
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    public boolean contains(Partition<N> partition) {
        mutex.readLock().lock();
        try {
            return ring.containsValue(partition);
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public List<ShardingRange<N>> findRanges(String nodeId) {
        mutex.readLock().lock();
        try {
            return getAllRanges()
                    .stream()
                    .filter(range -> Objects.equals(nodeId, range.getPartition().getNodeKey()))
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
            this.ranges = Collections.unmodifiableList(refreshRanges());
            return this.ranges;
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public EventWatch<ShardingListener<N>> event() {
        return notifier;
    }

    @Override
    public long getMaxSlots() {
        return maxSlots;
    }

    private void resetRanges() {
        this.ranges = null;
    }

    private List<ShardingRange<N>> refreshRanges() {
        Map.Entry<Long, Partition<N>> lastEntry = ring.lastEntry();
        if (lastEntry == null) {
            return Collections.emptyList();
        }
        List<ShardingRange<N>> ranges = new ArrayList<>();
        Partition<N> prev = lastEntry.getValue();
        for (Partition<N> partition : ring.values()) {
            ranges.add(new ShardingRange<>(prev, partition, maxSlots));
            prev = partition;
        }
        return ranges;
    }

    @Override
    public List<Partition<N>> findPartitions(String nodeId) {
        mutex.readLock().lock();
        try {
            return ring.values()
                    .stream()
                    .filter(partition -> Objects.equals(nodeId, partition.getNodeKey()))
                    .collect(Collectors.toList());
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public Optional<Partition<N>> prevPartition(long slot) {
        mutex.readLock().lock();
        try {
            Entry<Long, Partition<N>> entry = ring.lowerEntry(slot);
            if (entry == null) {
                entry = ring.lastEntry();
            }
            if (entry == null) {
                return Optional.empty();
            }
            return Optional.of(entry.getValue());
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public Optional<Partition<N>> nextPartition(long slot) {
        mutex.readLock().lock();
        try {
            Entry<Long, Partition<N>> entry = ring.higherEntry(slot);
            if (entry == null) {
                entry = ring.firstEntry();
            }
            if (entry == null) {
                return Optional.empty();
            }
            return Optional.of(entry.getValue());
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public Optional<Partition<N>> locate(String key) {
        mutex.readLock().lock();
        try {
            return findPartition(key);
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public List<Partition<N>> locate(String key, int count) {
        mutex.readLock().lock();
        try {
            return findPartition(key, count).collect(Collectors.toList());
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public int partitionSize() {
        mutex.readLock().lock();
        try {
            return ring.size();
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public List<Partition<N>> getAllPartitions() {
        mutex.readLock().lock();
        try {
            return new ArrayList<>(ring.values());
        } finally {
            mutex.readLock().unlock();
        }
    }

    private long hash(String key) {
        return hash(key, 0);
    }

    private long hash(String key, int seed) {
        return Math.abs(hasher.hash(key, seed, maxSlots));
    }

    private Optional<Partition<N>> findPartition(String key) {
        return findPartition(key, 1).findAny();
    }

    private Stream<Partition<N>> findPartition(String key, int count) {
        List<Partition<N>> res = new ArrayList<>();
        if (key != null && count > 0) {
            if (count < ring.size()) {
                long slot = hash(key);
                Iterator<Partition<N>> it = new ClockwiseIterator(slot, count);
                while (it.hasNext()) {
                    res.add(it.next());
                }
            } else {
                res.addAll(ring.values());
            }
        }
        LOGGER.debug("Ring [{}]: key [{}] located nodes [{}]", name, key, res);
        return res.stream();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ConsistentHashRing.class.getSimpleName() + "[", "]")
                .add("name= '" + name + "'")
                .add("algorithm= " + hasher)
                .add("partitionSize= " + partitionSize())
                .toString();
    }

    private class ClockwiseIterator implements Iterator<Partition<N>> {

        private final Iterator<Partition<N>> head;

        private final Iterator<Partition<N>> tail;

        private int count;

        private final boolean limit;

        public ClockwiseIterator(long slot, int count) {
            this.count = count;
            this.limit = count > 0;
            this.head = ring.headMap(slot).values().iterator();
            this.tail = ring.tailMap(slot).values().iterator();
        }

        @Override
        public boolean hasNext() {
            if (this.limit) {
                return (head.hasNext() || tail.hasNext()) && count > 0;
            } else {
                return (head.hasNext() || tail.hasNext());
            }
        }

        @Override
        public Partition<N> next() {
            if (this.limit && count <= 0) {
                return null;
            }
            try {
                return tail.hasNext() ? tail.next() : head.next();
            } finally {
                if (this.limit) {
                    count--;
                }
            }
        }

    }

}
