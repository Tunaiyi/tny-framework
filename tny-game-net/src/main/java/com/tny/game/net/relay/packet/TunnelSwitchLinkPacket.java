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
