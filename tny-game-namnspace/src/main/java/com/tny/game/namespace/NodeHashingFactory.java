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
import com.tny.game.namespace.consistenthash.*;

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
