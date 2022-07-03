package com.tny.game.net.endpoint;

import com.tny.game.net.base.*;

/**
 * <p>
 */
public interface Client<UID> extends Terminal<UID> {

    ClientConnectFuture<UID> open();

    void reconnect();
    
}
