/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.link;

/**
 * 远程(客户端连接不在本地)转发服务
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:31 下午
 */
public interface RelayExplorer<T extends RelayTunnel<?>> {

    /**
     * 获取指定的 tunnel
     *
     * @param instanceId 创建 tunnel 的服务实例 id
     * @param tunnelId   管道 id
     */
    T getTunnel(long instanceId, long tunnelId);

    /**
     * 关闭指定的 tunnel
     *
     * @param instanceId 创建 tunnel 的服务实例id
     * @param tunnelId   tunnel id
     */
    void closeTunnel(long instanceId, long tunnelId);

}
