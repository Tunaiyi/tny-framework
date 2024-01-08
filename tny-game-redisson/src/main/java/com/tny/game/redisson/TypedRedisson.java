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
package com.tny.game.redisson;

import org.redisson.api.*;
import org.redisson.api.redisnode.*;
import org.redisson.client.codec.Codec;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/12 7:52 下午
 */
public abstract class TypedRedisson<V> {

    private Codec codec;

    private RedissonClient redissonClient;

    public TypedRedisson() {
    }

    public TypedRedisson(Codec codec, RedissonClient redissonClient) {
        this.codec = codec;
        this.redissonClient = redissonClient;
    }

    public <L> RTimeSeries<V, L> getTimeSeries(String name) {
        return this.redissonClient.getTimeSeries(name, this.codec);
    }

    public <K> RStream<K, V> getStream(String name) {
        return this.redissonClient.getStream(name, this.codec);
    }

    public RRateLimiter getRateLimiter(String name) {
        return this.redissonClient.getRateLimiter(name);
    }

    public RBinaryStream getBinaryStream(String name) {
        return this.redissonClient.getBinaryStream(name);
    }

    public RGeo<V> getGeo(String name) {
        return this.redissonClient.getGeo(name);
    }

    public RSetCache<V> getSetCache(String name) {
        return this.redissonClient.getSetCache(name, this.codec);
    }

    public <K> RMapCache<K, V> getMapCache(String name, MapCacheOptions<K, V> options) {
        return this.redissonClient.getMapCache(name, this.codec, options);
    }

    public <K> RMapCache<K, V> getMapCache(String name) {
        return this.redissonClient.getMapCache(name);
    }

    public RBucket<V> getBucket(String name) {
        return this.redissonClient.getBucket(name, this.codec);
    }

    public RBuckets getBuckets() {
        return this.redissonClient.getBuckets(this.codec);
    }

    public RHyperLogLog<V> getHyperLogLog(String name) {
        return this.redissonClient.getHyperLogLog(name, this.codec);
    }

    public RList<V> getList(String name) {
        return this.redissonClient.getList(name, this.codec);
    }

    public <K> RListMultimap<K, V> getListMultimap(String name) {
        return this.redissonClient.getListMultimap(name, this.codec);
    }

    public <K> RListMultimapCache<K, V> getListMultimapCache(String name) {
        return this.redissonClient.getListMultimapCache(name, this.codec);
    }

    public <K> RLocalCachedMap<K, V> getLocalCachedMap(String name, LocalCachedMapOptions<K, V> options) {
        return this.redissonClient.getLocalCachedMap(name, this.codec, options);
    }

    public <K> RMap<K, V> getMap(String name) {
        return this.redissonClient.getMap(name, this.codec);
    }

    public <K> RMap<K, V> getMap(String name, MapOptions<K, V> options) {
        return this.redissonClient.getMap(name, this.codec, options);
    }

    public <K> RSetMultimap<K, V> getSetMultimap(String name) {
        return this.redissonClient.getSetMultimap(name, this.codec);
    }

    public <K> RSetMultimapCache<K, V> getSetMultimapCache(String name) {
        return this.redissonClient.getSetMultimapCache(name, this.codec);
    }

    public RSemaphore getSemaphore(String name) {
        return this.redissonClient.getSemaphore(name);
    }

    public RPermitExpirableSemaphore getPermitExpirableSemaphore(String name) {
        return this.redissonClient.getPermitExpirableSemaphore(name);
    }

    public RLock getLock(String name) {
        return this.redissonClient.getLock(name);
    }

    public RLock getMultiLock(RLock... locks) {
        return this.redissonClient.getMultiLock(locks);
    }

    //    public RLock getRedLock(RLock... locks) {
    //        return redissonClient.getRedLock(locks);
    //    }

    public RLock getFairLock(String name) {
        return this.redissonClient.getFairLock(name);
    }

    public RReadWriteLock getReadWriteLock(String name) {
        return this.redissonClient.getReadWriteLock(name);
    }

    public RSet<V> getSet(String name) {
        return this.redissonClient.getSet(name, this.codec);
    }

    public RSortedSet<V> getSortedSet(String name) {
        return this.redissonClient.getSortedSet(name, this.codec);
    }

    public RScoredSortedSet<V> getScoredSortedSet(String name) {
        return this.redissonClient.getScoredSortedSet(name, this.codec);
    }

    public RLexSortedSet getLexSortedSet(String name) {
        return this.redissonClient.getLexSortedSet(name);
    }

    public RTopic getTopic(String name) {
        return this.redissonClient.getTopic(name, this.codec);
    }

    public RPatternTopic getPatternTopic(String pattern) {
        return this.redissonClient.getPatternTopic(pattern, this.codec);
    }

    public RQueue<V> getQueue(String name) {
        return this.redissonClient.getQueue(name, this.codec);
    }

    public RTransferQueue<V> getTransferQueue(String name) {
        return this.redissonClient.getTransferQueue(name, this.codec);
    }

    public RDelayedQueue<V> getDelayedQueue(RQueue<V> destinationQueue) {
        return this.redissonClient.getDelayedQueue(destinationQueue);
    }

    public RRingBuffer<V> getRingBuffer(String name) {
        return this.redissonClient.getRingBuffer(name, this.codec);
    }

    public RPriorityQueue<V> getPriorityQueue(String name) {
        return this.redissonClient.getPriorityBlockingQueue(name, this.codec);
    }

    public RPriorityBlockingQueue<V> getPriorityBlockingQueue(String name) {
        return this.redissonClient.getPriorityBlockingQueue(name, this.codec);
    }

    public RPriorityBlockingDeque<V> getPriorityBlockingDeque(String name) {
        return this.redissonClient.getPriorityBlockingDeque(name, this.codec);
    }

    public RPriorityDeque<V> getPriorityDeque(String name) {
        return this.redissonClient.getPriorityDeque(name, this.codec);
    }

    public RBlockingQueue<V> getBlockingQueue(String name) {
        return this.redissonClient.getBlockingQueue(name, this.codec);
    }

    public RBoundedBlockingQueue<V> getBoundedBlockingQueue(String name) {
        return this.redissonClient.getBoundedBlockingQueue(name, this.codec);
    }

    public RDeque<V> getDeque(String name) {
        return this.redissonClient.getDeque(name, this.codec);
    }

    public RBlockingDeque<V> getBlockingDeque(String name) {
        return this.redissonClient.getBlockingDeque(name, this.codec);
    }

    public RAtomicLong getAtomicLong(String name) {
        return this.redissonClient.getAtomicLong(name);
    }

    public RAtomicDouble getAtomicDouble(String name) {
        return this.redissonClient.getAtomicDouble(name);
    }

    public RLongAdder getLongAdder(String name) {
        return this.redissonClient.getLongAdder(name);
    }

    public RDoubleAdder getDoubleAdder(String name) {
        return this.redissonClient.getDoubleAdder(name);
    }

    public RCountDownLatch getCountDownLatch(String name) {
        return this.redissonClient.getCountDownLatch(name);
    }

    public RBitSet getBitSet(String name) {
        return this.redissonClient.getBitSet(name);
    }

    public RBloomFilter<V> getBloomFilter(String name) {
        return this.redissonClient.getBloomFilter(name, this.codec);
    }

    public RScript getScript() {
        return this.redissonClient.getScript(this.codec);
    }

    public RScript getScript(Codec codec) {
        return this.redissonClient.getScript(codec);
    }

    public RScheduledExecutorService getExecutorService(String name) {
        return this.redissonClient.getExecutorService(name);
    }

    public RScheduledExecutorService getExecutorService(String name, ExecutorOptions options) {
        return this.redissonClient.getExecutorService(name, options);
    }

    public RTransaction createTransaction(TransactionOptions options) {
        return this.redissonClient.createTransaction(options);
    }

    public RBatch createBatch(BatchOptions options) {
        return this.redissonClient.createBatch(options);
    }

    public RBatch createBatch() {
        return this.redissonClient.createBatch();
    }

    public RKeys getKeys() {
        return this.redissonClient.getKeys();
    }

    public RLiveObjectService getLiveObjectService() {
        return this.redissonClient.getLiveObjectService();
    }

    public <T extends BaseRedisNodes> T getRedisNodes(RedisNodes<T> nodes) {
        return this.redissonClient.getRedisNodes(nodes);
    }

    public boolean isShutdown() {
        return this.redissonClient.isShutdown();
    }

    public boolean isShuttingDown() {
        return this.redissonClient.isShuttingDown();
    }

    public String getId() {
        return this.redissonClient.getId();
    }

    public RedissonClient getClient() {
        return this.redissonClient;
    }

    public TypedRedisson<V> setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        return this;
    }

    public Codec getCodec() {
        return this.codec;
    }

    public TypedRedisson<V> setCodec(Codec codec) {
        this.codec = codec;
        return this;
    }

}
