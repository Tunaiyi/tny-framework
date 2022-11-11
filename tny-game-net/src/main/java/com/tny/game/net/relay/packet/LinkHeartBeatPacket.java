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
public class LinkHeartBeatPacket extends BaseLinkPacket<LinkVoidArguments> {

    public static final RelayPacketFactory<LinkHeartBeatPacket, LinkVoidArguments> PING_FACTORY =
            (id, args, time) -> LinkHeartBeatPacket.ping(id, time);

    public static final RelayPacketFactory<LinkHeartBeatPacket, LinkVoidArguments> PONG_FACTORY =
            (id, args, time) -> LinkHeartBeatPacket.pong(id, time);

    public static LinkHeartBeatPacket ping(int id) {
        return new LinkHeartBeatPacket(id, RelayPacketType.LINK_PING);
    }

    public static LinkHeartBeatPacket pong(int id) {
        return new LinkHeartBeatPacket(id, RelayPacketType.LINK_PONG);
    }

    public static LinkHeartBeatPacket ping(int id, long time) {
        return new LinkHeartBeatPacket(id, RelayPacketType.LINK_PING, time);
    }

    public static LinkHeartBeatPacket pong(int id, long time) {
        return new LinkHeartBeatPacket(id, RelayPacketType.LINK_PONG, time);
    }

    private LinkHeartBeatPacket(int id, RelayPacketType type) {
        super(id, type, LinkVoidArguments.of());
    }

    private LinkHeartBeatPacket(int id, RelayPacketType type, long time) {
        super(id, type, time, LinkVoidArguments.of());
    }

    @Override
    protected String toPacketMessage() {
        return this.getType() == RelayPacketType.LINK_PING ? "Link # 心跳检测 [Ping]" : "Link # 心跳检测 [Pong]";
    }

}
