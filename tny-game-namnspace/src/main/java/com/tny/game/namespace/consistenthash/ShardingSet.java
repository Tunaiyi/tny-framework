/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.namespace.consistenthash;

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