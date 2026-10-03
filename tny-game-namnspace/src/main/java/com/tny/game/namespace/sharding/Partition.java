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
