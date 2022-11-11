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
public class LinkClosePacket extends BaseLinkPacket<LinkVoidArguments> {

    public static final RelayPacketFactory<LinkClosePacket, LinkVoidArguments> FACTORY = LinkClosePacket::new;

    public LinkClosePacket(int id) {
        super(id, RelayPacketType.LINK_CLOSE, LinkVoidArguments.of());
    }

    public LinkClosePacket(int id, long time) {
        super(id, RelayPacketType.LINK_CLOSE, time, LinkVoidArguments.of());
    }

    public LinkClosePacket(int id, LinkVoidArguments arguments, long time) {
        super(id, RelayPacketType.LINK_CLOSE, time, arguments);
    }

    @Override
    protected String toPacketMessage() {
        return "Link # 关闭连接";
    }

}
