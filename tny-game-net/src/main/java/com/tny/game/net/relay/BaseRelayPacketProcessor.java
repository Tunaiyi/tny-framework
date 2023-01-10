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
package com.tny.game.net.relay;

import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.exception.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/24 2:13 下午
 */
public abstract class BaseRelayPacketProcessor implements RelayPacketProcessor {

    public static final Logger LOGGER = LoggerFactory.getLogger(RelayPacketProcessor.class);

    private final RelayExplorer<? extends RelayTunnel<?>> relayLinkExplorer;

    protected BaseRelayPacketProcessor(RelayExplorer<? extends RelayTunnel<?>> relayLinkExplorer) {
        this.relayLinkExplorer = relayLinkExplorer;
    }

    @Override
    public void onLinkOpened(NetRelayLink link, LinkOpenedPacket packet) {
        checkLink(link, packet);
        LinkOpenedArguments arguments = packet.getArguments();
        if (arguments.isSuccess()) {
            LOGGER.info("#{} [ 连接成功 ]", link);
            link.open();
        } else {
            LOGGER.info("#{} [ 连接失败 ]", link);
            link.close();
        }
    }

    @Override
    public void onLinkClose(NetRelayLink link, LinkClosePacket packet) {
        checkLink(link, packet);
        LOGGER.info("#{} [ 关闭连接 ]", link);
        link.close();
    }

    @Override
    public void onLinkHeartBeat(NetRelayLink link, LinkHeartBeatPacket packet) {
        checkLink(link, packet);
        if (packet.getType() == RelayPacketType.LINK_PING) {
            link.pong();
        }
        link.heartbeat();
    }

    @Override
    public void onTunnelConnected(NetRelayLink link, TunnelConnectedPacket packet) {
        TunnelConnectedArguments arguments = packet.getArguments();
        if (arguments.isSuccess()) {
            LOGGER.info("#{} [ Tunnel({}) 连接成功 ]", link, arguments.getTunnelId());
        } else {
            LOGGER.info("#{} [ Tunnel({}) 连接失败 ]", link, arguments.getTunnelId());
            relayLinkExplorer.closeTunnel(arguments.getInstanceId(), arguments.getTunnelId());
        }
    }

    @Override
    public void onTunnelDisconnect(NetRelayLink link, TunnelDisconnectPacket packet) {
        checkLink(link, packet);
        TunnelVoidArguments arguments = packet.getArguments();
        LOGGER.info("#{} [ Tunnel({}) 连接断开 ]", link.getId(), arguments.getTunnelId());
        relayLinkExplorer.closeTunnel(arguments.getInstanceId(), arguments.getTunnelId());
    }

    protected void checkLink(NetRelayLink link, RelayPacket<?> packet) {
        if (link == null) {
            RelayPacket.release(packet);
            throw new RelayLinkNoFoundException("socket未创建转发连接NetRelayLink");
        }
    }

}
