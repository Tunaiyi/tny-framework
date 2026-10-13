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
package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.sharding.*;

/**
 * 一致性节点Hash环 工厂
 * <p>
 *
 * @author kgtny
 * @date 2022/7/21 10:30
 **/
public class EtcdNodeHashingRingFactory implements NodeHashingFactory {

    private static final EtcdNodeHashingRingFactory FACTORY = new EtcdNodeHashingRingFactory();

    public static NodeHashingFactory getDefault() {
        return FACTORY;
    }

    @Override
    public <N extends ShardingNode> NodeHashing<N> create(String rootPath, HashingOptions<N> option, NamespaceExplorer explorer,
            ObjectCodecAdapter adapter) {
        return new EtcdNodeHashingRing<>(rootPath, option, explorer, adapter);
    }

}
