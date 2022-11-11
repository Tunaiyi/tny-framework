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

import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 12:31 下午
 */
public interface RelayPacketProcessor {

    void onLinkOpen(RelayTransporter transporter, LinkOpenPacket packet);

    void onLinkOpened(NetRelayLink link, LinkOpenedPacket packet);

    void onLinkClose(NetRelayLink link, LinkClosePacket packet);

    void onLinkHeartBeat(NetRelayLink link, LinkHeartBeatPacket packet);

    void onTunnelConnect(NetRelayLink link, TunnelConnectPacket packet);

    void onTunnelConnected(NetRelayLink link, TunnelConnectedPacket packet);

    void onTunnelDisconnect(NetRelayLink link, TunnelDisconnectPacket packet);

    void onTunnelSwitchLink(NetRelayLink link, TunnelSwitchLinkPacket packet);

    void onTunnelRelay(NetRelayLink link, TunnelRelayPacket packet);

}
