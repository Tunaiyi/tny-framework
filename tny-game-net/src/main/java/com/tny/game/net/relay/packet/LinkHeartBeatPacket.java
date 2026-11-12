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
