package com.tny.game.net.session;

import com.tny.game.common.enums.*;

public enum TunnelConnectorStatus implements IntEnumerable {

    INITIAL(0),
    CONNECTING(1),
    RECONNECTING(2),
    CONNECTED(3),
    DISCONNECT(4),
    CLOSE(5),

    //
    ;

    private final int id;


    private TunnelConnectorStatus(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

}
