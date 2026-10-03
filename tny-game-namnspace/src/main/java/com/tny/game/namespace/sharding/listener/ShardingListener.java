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
package com.tny.game.namespace.sharding.listener;

import com.tny.game.namespace.sharding.*;

import java.util.List;

/**
 * 分片监听器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 02:46
 **/
public interface ShardingListener<N extends ShardingNode> {

    /**
     * 增加分片改变
     *
     * @param sharding 改变分片
     */
    void onChange(Sharding<N> sharding, List<Partition<N>> partitions);

    /**
     * 删除分片改变
     *
     * @param sharding 改变分片
     */
    void onRemove(Sharding<N> sharding, List<Partition<N>> partitions);

}
