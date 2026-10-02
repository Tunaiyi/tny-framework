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
        // 原 super(tunnelId, instanceId) 与父类形参序颠倒：本地 getter 互换值。
        // 修复与 TunnelConnectedArgumentsProto 的槽位读写对调同提交落地——线上字节语义逐位不变（组 12 自抵消）
        super(instanceId, tunnelId);
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
