/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.namespace.sharding;

import com.tny.game.common.event.*;
import com.tny.game.namespace.sharding.listener.*;

import java.util.*;

/**
 * 分片
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 16:27
 **/
public interface Sharding<N extends ShardingNode> {

    /**
     * 节点是否存在
     *
     * @param partition 分区
     * @return 存在返回 ture, 否则返回 false
     */
    boolean contains(Partition<N> partition);

    /**
     * 获取节点对应的分区列表
     *
     * @param nodeId 节点id
     * @return 返回分区列表
     */
    List<Partition<N>> findPartitions(String nodeId);

    /**
     * 获取节点对应的分区列表
     *
     * @param nodeId 节点id
     * @return 返回分区列表
     */
    List<ShardingRange<N>> findRanges(String nodeId);

    /**
     * 获取所有分区列表
     *
     * @return 返回分区列表
     */
    List<ShardingRange<N>> getAllRanges();

    /**
     * 获取指定槽位的前一个分区(不包含 slot 分区)
     *
     * @param slot 指定槽位
     * @return 返回前一个分区
     */
    Optional<Partition<N>> prevPartition(long slot);

    /**
     * 获取指定槽位的后一个分区(不包含 slot 分区)
     *
     * @param slot 指定槽位
     * @return 返回后一个分区
     */
    Optional<Partition<N>> nextPartition(long slot);

    /**
     * @return 获取所有分区
     */
    List<Partition<N>> getAllPartitions();

    /**
     * 更具 Key 定位节点
     *
     * @param key 定位的 key
     * @return 返回节点
     */
    Optional<Partition<N>> locate(String key);

    /**
     * 更具 Key 定位返回指定数量的节点
     *
     * @param key   定位的 key
     * @param count 返回节点数量
     * @return 返回节点列表
     */
    List<Partition<N>> locate(String key, int count);

    /**
     * @return 返回丰满区数量
     */
    int partitionSize();

    /**
     * @return 分区改变事件
     */
    EventWatch<ShardingListener<N>> event();

    /**
     * 最大槽数
     */
    long getMaxSlots();

}