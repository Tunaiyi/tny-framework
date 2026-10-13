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
package com.tny.game.namespace;

import com.tny.game.codec.*;
import com.tny.game.namespace.sharding.*;

/**
 * 节点哈希工厂方法
 * <p>
 *
 * @author kgtny
 * @date 2022/7/21 09:56
 **/
public interface NodeHashingFactory {

    /**
     * 创建节点哈希器
     *
     * @param rootPath 根路径
     * @param option   选项
     * @param explorer 命名空间浏览器
     * @param adapter  对象编解码
     * @return 返回创建节点哈希器
     */
    <N extends ShardingNode> NodeHashing<N> create(String rootPath, HashingOptions<N> option, NamespaceExplorer explorer,
            ObjectCodecAdapter adapter);

}
