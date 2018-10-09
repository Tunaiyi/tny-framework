package com.tny.game.net.transport;

import com.tny.game.common.event.*;
import com.tny.game.net.transport.listener.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-03 15:55
 */
public class TunnelEvents {

    @SuppressWarnings("unchecked")
    protected static final BindVoidEventBus<TunnelOpenListener, Tunnel> ON_OPEN=
            EventBuses.of(TunnelOpenListener.class, TunnelOpenListener::onOpen);

    @SuppressWarnings("unchecked")
    protected static final BindVoidEventBus<TunnelCloseListener, Tunnel> ON_CLOSE =
            EventBuses.of(TunnelCloseListener.class, TunnelCloseListener::onClose);

    protected TunnelEvents() {
    }

    public static ListenerRegister<TunnelOpenListener> openEventBus() {
        return ON_OPEN;
    }

    public static ListenerRegister<TunnelCloseListener> closeEventBus() {
        return ON_CLOSE;
    }

}
