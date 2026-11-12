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

    private final RelayExplorer relayLinkExplorer;

    protected BaseRelayPacketProcessor(RelayExplorer relayLinkExplorer) {
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
