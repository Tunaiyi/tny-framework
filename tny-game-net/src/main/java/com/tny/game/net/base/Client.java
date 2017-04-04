package com.tny.game.net.base;

import com.tny.game.net.tunnel.Terminal;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public interface Client<UID> extends Terminal<UID> {

    String getName();

    void reconnect();

    void disconnect();

    boolean isConnected();
}
