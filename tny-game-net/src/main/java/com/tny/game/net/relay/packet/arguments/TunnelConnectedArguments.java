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

package com.tny.game.net.relay.packet.arguments;

import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:05 下午
 */
public class TunnelConnectedArguments extends BaseTunnelPacketArguments {

    private final boolean result;

    public static TunnelConnectedArguments success(RelayTunnel tunnel) {
        return new TunnelConnectedArguments(tunnel.getInstanceId(), tunnel.getId(), true);
    }

    public static TunnelConnectedArguments failure(RelayTunnel tunnel) {
        return new TunnelConnectedArguments(tunnel.getInstanceId(), tunnel.getId(), false);
    }

    public static TunnelConnectedArguments success(long instanceId, long tunnelId) {
        return new TunnelConnectedArguments(instanceId, tunnelId, true);
    }

    public static TunnelConnectedArguments failure(long instanceId, long tunnelId) {
        return new TunnelConnectedArguments(instanceId, tunnelId, false);
    }

    public static TunnelConnectedArguments ofResult(long instanceId, long tunnelId, boolean result) {
        return new TunnelConnectedArguments(instanceId, tunnelId, result);
    }

    private TunnelConnectedArguments(long instanceId, long tunnelId, boolean result) {
        super(tunnelId, instanceId);
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }

    public boolean isSuccess() {
        return result;
    }

    public boolean isFailure() {
        return !result;
    }

}
