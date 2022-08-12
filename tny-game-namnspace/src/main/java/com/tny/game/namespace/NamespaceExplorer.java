/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.namespace;

import com.tny.game.codec.*;
import com.tny.game.common.type.*;
import com.tny.game.namespace.consistenthash.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Namespace 管理器
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 04:07
 **/
public interface NamespaceExplorer {

    /**
     * 获取指定 path 节点
     *
     * @param path 路径
     * @param type 值类型
     * @param <T>  值类型
     * @return 返回获取的节点 Future, 如果没有会Future返回值为 null
     */
    <T> CompletableFuture<NameNode<T>> get(String path, ObjectMineType<T> type);

    /**
     * 匹配路径查找指定节点列表
     *
     * @param path 匹配路径
     * @param type 值MineType
     * @param <T>  值类型
     * @return 返回获取的节点List的 Future, 如果没有会Future返回值为 空List
     */
    <T> CompletableFuture<List<NameNode<T>>> findAll(String path, ObjectMineType<T> type);

    /**
     * 匹配路径查找指定节点列表
     *
     * @param from 起始 (包括)
     * @param to   结束 (不包括)
     * @param type 值MineType
     * @param <T>  值类型
     * @return 返回获取的节点List的 Future, 如果没有会Future返回值为 空List
     */
    <T> CompletableFuture<List<NameNode<T>>> findAll(String from, String to, ObjectMineType<T> type);

    /**
     * 创建 hash 节点存储器
     *
     * @param rootPath 路径
     * @param options  选项
     * @return 返回一致性 hash 环
     */
    default <T extends ShardingNode> NodeHashing<T> nodeHashing(String rootPath, HashingOptions<T> options) {
        return nodeHashing(rootPath, null, options);
    }

    /**
     * 创建 hash 节点存储器
     *
     * @param rootPath 路径
     * @param factory  节点存储器工厂
     * @param options  选项
     * @return 返回一致性 hash 环
     */
    <T extends ShardingNode> NodeHashing<T> nodeHashing(String rootPath, NodeHashingFactory factory, HashingOptions<T> options);

    /**
     * 创建 hash 节点存储器
     *
     * @param rootPath   路径
     * @param keyHasher  key哈希计算器
     * @param nodeHasher 节点哈希计算器
     * @param type       分区类型
     * @return 返回 hash 节点存储器
     */
    default <T extends ShardingNode> NodeHashing<T> nodeHashing(String rootPath, long maxSlotSize,
            Hasher<String> keyHasher, Hasher<PartitionedNode<T>> nodeHasher, ReferenceType<PartitionedNode<T>> type) {
        return nodeHashing(rootPath, maxSlotSize, keyHasher, nodeHasher, type, null, null);
    }

    /**
     * 创建 hash 节点存储器
     *
     * @param rootPath   路径
     * @param keyHasher  key哈希计算器
     * @param nodeHasher 节点哈希计算器
     * @param type       分区类型
     * @param factory    节点存储器工厂
     * @return 返回 hash 节点存储器
     */
    default <T extends ShardingNode> NodeHashing<T> nodeHashing(String rootPath, long maxSlotSize,
            Hasher<String> keyHasher, Hasher<PartitionedNode<T>> nodeHasher, ReferenceType<PartitionedNode<T>> type, NodeHashingFactory factory) {
        return nodeHashing(rootPath, maxSlotSize, keyHasher, nodeHasher, type, factory, null);
    }

    /**
     * 创建 hash 节点存储器
     *
     * @param rootPath   路径
     * @param keyHasher  key哈希计算器
     * @param nodeHasher 节点哈希计算器
     * @param type       分区类型
     * @param custom     选项自定义
     * @return 返回 hash 节点存储器
     */
    default <T extends ShardingNode> NodeHashing<T> nodeHashing(String rootPath, long maxSlotSize,
            Hasher<String> keyHasher, Hasher<PartitionedNode<T>> nodeHasher, ReferenceType<PartitionedNode<T>> type,
            Consumer<HashingOptions.Builder<T>> custom) {
        return nodeHashing(rootPath, maxSlotSize, keyHasher, nodeHasher, type, null, custom);
    }

    /**
     * 创建 hash 节点存储器
     *
     * @param rootPath   路径
     * @param keyHasher  key哈希计算器
     * @param nodeHasher 节点哈希计算器
     * @param type       分区类型
     * @param factory    节点存储器工厂
     * @param custom     选项自定义
     * @return 返回 hash 节点存储器
     */
    default <T extends ShardingNode> NodeHashing<T> nodeHashing(String rootPath, long maxSlotSize,
            Hasher<String> keyHasher, Hasher<PartitionedNode<T>> nodeHasher, ReferenceType<PartitionedNode<T>> type,
            NodeHashingFactory factory, Consumer<HashingOptions.Builder<T>> custom) {
        var optionBuilder = hashingOptions(type, maxSlotSize, keyHasher, nodeHasher);
        if (custom != null) {
            custom.accept(optionBuilder);
        }
        return nodeHashing(rootPath, factory, optionBuilder.build());
    }

    /**
     * Hashing 选项 Builder
     *
     * @param type       类型
     * @param keyHasher  key哈希计算器
     * @param nodeHasher 节点哈希计算器
     * @return 选项 Build
     */
    default <T extends ShardingNode> HashingOptions.Builder<T> hashingOptions(
            ReferenceType<PartitionedNode<T>> type, long maxSlotSize, Hasher<String> keyHasher, Hasher<PartitionedNode<T>> nodeHasher) {
        return HashingOptions.newBuilder(type, maxSlotSize, keyHasher, nodeHasher);
    }

    /**
     * 创建 Hashing 节点订阅器
     *
     * @param parentPath  父路径
     * @param maxSlotSize 最大槽位
     * @param mineType    媒体类型
     * @return 返回订阅器
     */
    <T> HashingSubscriber<T> hashingSubscriber(String parentPath, long maxSlotSize, ObjectMineType<T> mineType);

    /**
     * 创建 Hashing 节点发布器
     *
     * @param parentPath 父路径
     * @param keyHasher  key hash 计算器
     * @param mineType   媒体类型
     * @return 返回发布器
     */
    <K, T> HashingPublisher<K, T> hashingPublisher(String parentPath, long maxSlotSize, Hasher<T> keyHasher, ObjectMineType<T> mineType);

    /**
     * 创建节点监控器
     *
     * @param path 监听路径
     * @param type 值MineType
     * @param <T>  值类型
     * @return 返回节点的监控器
     */
    <T> NameNodesWatcher<T> nodeWatcher(String path, ObjectMineType<T> type);

    /**
     * 创建匹配节点监控器
     *
     * @param path 监听路径
     * @param type 值MineType
     * @param <T>  值类型
     * @return 返回匹配节点监控器
     */
    <T> NameNodesWatcher<T> allNodeWatcher(String path, ObjectMineType<T> type);

    /**
     * 创建匹配节点监控器
     *
     * @param from 起始 (包括)
     * @param to   结束 (不包括)
     * @param type 值MineType
     * @param <T>  值类型
     * @return 返回匹配节点监控器
     */
    <T> NameNodesWatcher<T> allNodeWatcher(String from, String to, ObjectMineType<T> type);

    /**
     * 创建租约
     *
     * @param name 姓名
     * @param ttl  租约
     * @return 返回结果
     */
    CompletableFuture<Lessee> lease(String name, long ttl);

    /**
     * 创建租约(60s)
     *
     * @param name 姓名
     * @return 返回结果
     */
    default CompletableFuture<Lessee> lease(String name) {
        return lease(name, 60000);
    }

    /**
     * 获取指定 path 的节点, 如果存在则获取, 不存在则创建.
     *
     * @param <T>   值类型     不存在时候插入的值.
     * @param path  路径
     * @param type  值类型Class
     * @param value 值
     * @return 返回获取的节点 Future, 如果存在则返回该节点, 如果不存在则返回创建的节点
     */
    <T> CompletableFuture<NameNode<T>> getOrAdd(String path, ObjectMineType<T> type, T value);

    /**
     * 获取指定 path 的节点, 如果存在则获取, 不存在则创建.
     *
     * @param <T>    值类型     不存在时候插入的值.
     * @param path   路径
     * @param type   值MineType
     * @param value  值
     * @param lessee 租客
     * @return 返回获取的节点 Future, 如果存在则返回该节点, 如果不存在则返回创建的节点
     */
    <T> CompletableFuture<NameNode<T>> getOrAdd(String path, ObjectMineType<T> type, T value, Lessee lessee);

    /**
     * 向指定 path 插入指定节点, 如果已存在则不插入.
     *
     * @param path  路径
     * @param value 值
     * @param <T>   值类型
     * @return 返回插入的节点 Future, 如果没有插入则会Future返回值为 null
     */
    <T> CompletableFuture<NameNode<T>> add(String path, ObjectMineType<T> type, T value);

    /**
     * 向指定 path 插入指定的租约节点, 如果已存在则不插入.
     *
     * @param path   路径
     * @param value  值
     * @param lessee 租客
     * @param <T>    值类型
     * @return 返回插入的节点 Future, 如果没有插入则会Future返回值为 null
     */
    <T> CompletableFuture<NameNode<T>> add(String path, ObjectMineType<T> type, T value, Lessee lessee);

    /**
     * 向指定 path 保存指定的节点. 无论存在与否都会插入
     *
     * @param path  路径
     * @param value 值
     * @param <T>   值类型
     * @return 返回保存的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> save(String path, ObjectMineType<T> type, T value);

    /**
     * 向指定 path 保存指定的租约节点. 无论存在与否都会插入
     *
     * @param path   路径
     * @param value  值
     * @param lessee 租客
     * @param <T>    值类型
     * @return 返回保存的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> save(String path, ObjectMineType<T> type, T value, Lessee lessee);

    /**
     * 如果指定 path 节点存在, 更新该节点.
     *
     * @param path  路径
     * @param value 值
     * @param <T>   值类型
     * @return 返回更新的节点 Future, 无更新则Future返回值为 null
     */
    <T> CompletableFuture<NameNode<T>> update(String path, ObjectMineType<T> type, T value);

    /**
     * 如果指定 path 节点存在, 更新该租约节点.
     *
     * @param path  路径
     * @param value 值
     * @param <T>   值类型
     * @return 返回更新的节点 Future, 无更新则Future返回值为 null
     */
    <T> CompletableFuture<NameNode<T>> update(String path, ObjectMineType<T> type, T value, Lessee lessee);

    /**
     * 如果指定 path 节点存在并且值等于expect值, 则更新该节点.
     *
     * @param path   路径
     * @param expect 期待值
     * @param value  值
     * @param <T>    值类型
     * @return 返回更新的节点 Future, 无更新则Future返回值为 null
     */
    <T> CompletableFuture<NameNode<T>> updateIf(String path, ObjectMineType<T> type, T expect, T value);

    /**
     * 如果指定 path 节点存在并且值等于expect值, 则更新该租约节点.
     *
     * @param path   路径
     * @param expect 期待值
     * @param value  值
     * @param lessee 租客
     * @param <T>    值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> updateIf(String path, ObjectMineType<T> type, T expect, T value, Lessee lessee);

    /**
     * 如果指定 path 节点存在并且节点版本等于指定version, 则更新该节点.
     *
     * @param path    路径
     * @param version 期待值
     * @param value   值
     * @param <T>     值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> updateIf(String path, long version, ObjectMineType<T> type, T value);

    /**
     * 如果指定 path 节点存在并且节点版本等于指定version, 则更新该租约节点.
     *
     * @param path    路径
     * @param version 期待值
     * @param value   值
     * @param lessee  租客
     * @param <T>     值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> updateIf(String path, long version, ObjectMineType<T> type, T value, Lessee lessee);

    /**
     * 如果指定 path 节点存在并且节点版本在指定区间内, 则更新该节点.
     *
     * @param path       路径
     * @param minVersion 最小值
     * @param minBorder  最小值边界
     * @param maxVersion 最大值
     * @param maxBorder  最大值边界
     * @param value      值
     * @param <T>        值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> updateIf(String path, long minVersion, RangeBorder minBorder, long maxVersion, RangeBorder maxBorder,
            ObjectMineType<T> type, T value);

    /**
     * 如果指定 path 节点存在并且节点版本在指定区间内, 则更新该租约节点.
     *
     * @param path       路径
     * @param minVersion 最小值
     * @param minBorder  最小值边界
     * @param maxVersion 最大值
     * @param maxBorder  最大值边界
     * @param value      值
     * @param lessee     租客
     * @param <T>        值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> updateIf(String path, long minVersion, RangeBorder minBorder, long maxVersion, RangeBorder maxBorder,
            ObjectMineType<T> type, T value, Lessee lessee);

    /**
     * 如果指定 path 节点存在,同时id等于指定id,且值等于expect值, 则更新该节点.
     *
     * @param path  路径
     * @param id    指定 id
     * @param value 值
     * @param <T>   值类型
     * @return 返回更新的节点 Future, 无更新则Future返回值为 null
     */
    <T> CompletableFuture<NameNode<T>> updateById(String path, long id, ObjectMineType<T> type, T value);

    /**
     * 如果指定 path 节点存在, 且id等于指定id, 等于指定expect值, 则更新该租约节点.
     *
     * @param path   路径
     * @param id     指定 id
     * @param value  值
     * @param lessee 租客
     * @param <T>    值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> updateById(String path, long id, ObjectMineType<T> type, T value, Lessee lessee);

    /**
     * 如果指定 path 节点存在, 且id等于指定id, 则更新该节点.
     *
     * @param path   路径
     * @param id     指定 id
     * @param expect 期待值
     * @param value  值
     * @param <T>    值类型
     * @return 返回更新的节点 Future, 无更新则Future返回值为 null
     */
    <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, ObjectMineType<T> type, T expect, T value);

    /**
     * 如果指定 path 节点存在,同时id等于指定id,等于指定expect值, 则更新该租约节点.
     *
     * @param path   路径
     * @param id     指定 id
     * @param expect 期待值
     * @param value  值
     * @param lessee 租客
     * @param <T>    值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, ObjectMineType<T> type, T expect, T value, Lessee lessee);

    /**
     * 如果指定 path 节点存在,同时id等于指定id,且版本等于指定version, 则更新该节点.
     *
     * @param path    路径
     * @param id      指定 id
     * @param version 期待值
     * @param value   值
     * @param <T>     值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long version, ObjectMineType<T> type, T value);

    /**
     * 如果指定 path 节点存在,同时id等于指定id,且版本等于指定version, 则更新该租约节点.
     *
     * @param path    路径
     * @param id      指定 id
     * @param version 期待值
     * @param value   值
     * @param lessee  租客
     * @param <T>     值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long version, ObjectMineType<T> type, T value, Lessee lessee);

    /**
     * 如果指定 path 节点存在,同时id等于指定id,并且节点版本在指定区间内, 则更新该节点.
     *
     * @param path       路径
     * @param minVersion 最小值
     * @param minBorder  最小值边界
     * @param maxVersion 最大值
     * @param maxBorder  最大值边界
     * @param value      值
     * @param <T>        值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long minVersion, RangeBorder minBorder, long maxVersion,
            RangeBorder maxBorder, ObjectMineType<T> type, T value);

    /**
     * 如果指定 path 节点存在,同时id等于指定id,并且节点版本在指定区间内, 则更新该租约节点.
     *
     * @param path       路径
     * @param minVersion 最小值
     * @param minBorder  最小值边界
     * @param maxVersion 最大值
     * @param maxBorder  最大值边界
     * @param lessee     租客
     * @param <T>        值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long minVersion, RangeBorder minBorder, long maxVersion,
            RangeBorder maxBorder, ObjectMineType<T> type, T value, Lessee lessee);

    /**
     * 删除指定 path 节点
     *
     * @param path 路径
     * @return 返回删除节点 Future
     */
    CompletableFuture<Boolean> remove(String path);

    /**
     * 删除匹配 path 的所有节点
     *
     * @param path 匹配路径
     * @return 返回删除节点 Future
     */
    CompletableFuture<Long> removeAll(String path);

    /**
     * 删除指定 path 节点
     *
     * @param path 路径
     * @return 返回删除节点 Future
     */
    <T> CompletableFuture<NameNode<T>> removeAndGet(String path, ObjectMineType<T> type);

    /**
     * 删除匹配 path 的所有节点
     *
     * @param path 匹配路径
     * @return 返回删除节点 Future
     */
    <T> CompletableFuture<List<NameNode<T>>> removeAllAndGet(String path, ObjectMineType<T> type);

    /**
     * 删除指定 path 节点, 且值等于expect值
     *
     * @param path   路径
     * @param expect 期望值
     * @return 返回删除节点 Future
     */
    <T> CompletableFuture<NameNode<T>> removeIf(String path, ObjectMineType<T> type, T expect);

    /**
     * 删除指定 path 节点, 且版本等于version值
     *
     * @param path    路径
     * @param version 期望版本
     * @return 返回删除节点 Future
     */
    <T> CompletableFuture<NameNode<T>> removeIf(String path, long version, ObjectMineType<T> type);

    /**
     * 删除指定 path 节点, 并且节点版本在指定区间内.
     *
     * @param path       路径
     * @param minVersion 最小值
     * @param minBorder  最小值边界
     * @param maxVersion 最大值
     * @param maxBorder  最大值边界
     * @param <T>        值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> removeIf(String path, long minVersion, RangeBorder minBorder, long maxVersion, RangeBorder maxBorder,
            ObjectMineType<T> type);

    /**
     * 删除指定 path 节点, 且id等于指定id
     *
     * @param path 路径
     * @param id   id
     * @return 返回删除节点 Future
     */
    <T> CompletableFuture<NameNode<T>> removeById(String path, long id, ObjectMineType<T> type);

    /**
     * 删除指定 path 节点, 且id等于指定id, 且值等于expect值
     *
     * @param path   路径
     * @param id     id
     * @param expect 期望值
     * @param type   期望值
     * @return 返回删除节点 Future
     */
    <T> CompletableFuture<NameNode<T>> removeByIdAndIf(String path, long id, ObjectMineType<T> type, T expect);

    /**
     * 删除指定 path 节点, 且id等于指定id, 且版本等于version值
     *
     * @param path    路径
     * @param id      id
     * @param version 期望版本
     * @param type    期望版本
     * @return 返回删除节点 Future
     */
    <T> CompletableFuture<NameNode<T>> removeByIdAndIf(String path, long id, long version, ObjectMineType<T> type);

    /**
     * 删除指定 path 节点, 同时id等于指定id,并且节点版本在指定区间内.
     *
     * @param path       路径
     * @param id         id
     * @param minVersion 最小值
     * @param minBorder  最小值边界
     * @param maxVersion 最大值
     * @param maxBorder  最大值边界
     * @param <T>        值类型
     * @return 返回更新的节点 Future
     */
    <T> CompletableFuture<NameNode<T>> removeByIdAndIf(String path, long id, long minVersion, RangeBorder minBorder, long maxVersion,
            RangeBorder maxBorder, ObjectMineType<T> type);

}
