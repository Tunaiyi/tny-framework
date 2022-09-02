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

/**
 * 分区
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 13:26
 **/
public interface Partition<N extends ShardingNode> {

    /**
     * @return 分区键值
     */
    String getKey();

    /**
     * @return 对应节点
     */
    N getNode();

    /**
     * @return 对应节点第几个分区
     */
    int getIndex();

    /**
     * @return 槽位id
     */
    long getSlot();

    default String getNodeKey() {
        N node = this.getNode();
        if (node != null) {
            return node.getKey();
        }
        return null;
    }

}
