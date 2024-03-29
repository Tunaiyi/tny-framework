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
package com.tny.game.net.relay.link.allot;

import com.tny.game.net.clusters.*;
import com.tny.game.net.transport.*;

/**
 * 转发目标服务实例分配策略
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 8:11 下午
 */
public interface ServeInstanceAllotStrategy {

    /**
     * 分配服务实例
     *
     * @param tunnel  分配的通讯管道
     * @param cluster 目标集群
     * @return 返回服务实例
     */
    RelayServeInstance allot(Tunnel tunnel, NetRemoteServeCluster cluster);

}