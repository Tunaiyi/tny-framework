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
package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelSwitchLinkPacket extends BaseTunnelPacket<TunnelVoidArguments> {

    public static final RelayPacketFactory<TunnelSwitchLinkPacket, TunnelVoidArguments> FACTORY = TunnelSwitchLinkPacket::new;

    public TunnelSwitchLinkPacket(int id, TunnelVoidArguments arguments) {
        super(id, RelayPacketType.TUNNEL_SWITCH_LINK, arguments);
    }

    public TunnelSwitchLinkPacket(int id, TunnelVoidArguments arguments, long time) {
        // 原硬编码 TUNNEL_CONNECT——工厂（唯一线上路径）产出的切换包被对端解释为"隧道建立"，
        // 故障转移销毁业务会话；类型修正要求网关/业务服成对升级（release-note）
        super(id, RelayPacketType.TUNNEL_SWITCH_LINK, time, arguments);
    }

    @Override
    protected String toTunnelPacketMessage() {
        return "切换Link";
    }

}
