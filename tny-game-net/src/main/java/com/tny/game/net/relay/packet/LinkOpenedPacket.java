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
