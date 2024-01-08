package com.tny.game.net.transport;

import com.tny.game.common.event.bus.*;
import com.tny.game.net.transport.listener.*;

public interface TunnelEventWatches {

    EventListen<TunnelActivateListener> activateWatch();

    EventListen<TunnelUnactivatedListener> unactivatedWatch();

    EventListen<TunnelCloseListener> closeWatch();

    EventListen<TunnelReceiveListener> receiveWatch();
}
