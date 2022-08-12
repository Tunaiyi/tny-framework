/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 3:30 下午
 */
public abstract class BaseTunnelPacketArguments implements TunnelPacketArguments {

    private final long tunnelId;

    private final long instanceId;

    protected BaseTunnelPacketArguments(long instanceId, long tunnelId) {
        this.instanceId = instanceId;
        this.tunnelId = tunnelId;
    }

    @Override
    public long getTunnelId() {
        return tunnelId;
    }

    @Override
    public long getInstanceId() {
        return instanceId;
    }

}
