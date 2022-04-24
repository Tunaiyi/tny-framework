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

}
