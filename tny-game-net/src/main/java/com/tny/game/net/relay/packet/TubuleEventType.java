package com.tny.game.net.relay.packet;

import com.tny.game.common.enums.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public enum TubuleEventType implements EnumIdentifiable<Byte> {

    MESSAGE(0),

    CONNECT(1),

    DISCONNECT(1 << 1),

    ;

    private final byte id;

    TubuleEventType(int id) {
        this.id = (byte)id;
    }

    @Override
    public Byte getId() {
        return this.id;
    }

    public byte getIdValue() {
        return this.id;
    }

}
