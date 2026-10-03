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

import java.util.*;

/**
 * 分片集合
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 16:27
 **/
public interface ShardingSet<P extends ShardingNode> extends Sharding<P> {

    /**
     * 添加分区
     *
     * @param partition 分区
     * @return 返回是否添加成功
     */
    boolean add(Partition<P> partition);

    /**
     * 更新分区
     *
     * @param partition 分区
     * @return 返回是否添加成功
     */
    boolean update(Partition<P> partition);

    /**
     * 保存分区
     *
     * @param partition 分区
     */
    void save(Partition<P> partition);

    /**
     * 批量加入节点
     *
     * @param partitions 节点列表
     * @return 返回加入的节点
     */
    List<Partition<P>> addAll(Collection<Partition<P>> partitions);

    /**
     * 删除节点
     *
     * @param slot 移除槽位
     * @return 返回移除节点关联的分区
     */
    Partition<P> remove(long slot);

    /**
     * 删除节点
     *
     * @param partition 删除的节点
     * @return 返回移除节点关联的分区
     */
    boolean remove(Partition<P> partition);

    /**
     * 清除
     */
    void clear();

}