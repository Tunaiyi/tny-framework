package com.tny.game.net.message;

import com.tny.game.net.tunnel.Tunnel;

public interface NetMessage<UID> extends Message<UID> {

    void sendBy(Tunnel<UID> tunnel);

}
