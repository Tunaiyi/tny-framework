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
 * @date : 2021/8/9 8:52 下午
 */
public class LinkOpenedPacket extends BaseLinkPacket<LinkOpenedArguments> {

    public static final RelayPacketFactory<LinkOpenedPacket, LinkOpenedArguments> FACTORY = LinkOpenedPacket::new;

    public LinkOpenedPacket(int id, boolean result) {
        super(id, RelayPacketType.LINK_OPENED, LinkOpenedArguments.of(result));
    }

    public LinkOpenedPacket(int id, LinkOpenedArguments arguments, long time) {
        super(id, RelayPacketType.LINK_OPENED, time, arguments);
    }

    @Override
    protected String toPacketMessage() {
        return arguments != null && arguments.getResult() ? "Link # 连接成功" : "Link # 连接失败";
    }

}
