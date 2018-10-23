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
    protected static final BindVoidEventBus<TunnelAuthenticateListener, Tunnel> ON_AUTHENTICATE=
            EventBuses.of(TunnelAuthenticateListener.class, TunnelAuthenticateListener::onAuthenticate);

    @SuppressWarnings("unchecked")
    protected static final BindVoidEventBus<TunnelOpenListener, Tunnel> ON_OPEN =
            EventBuses.of(TunnelOpenListener.class, TunnelOpenListener::onOpen);

    @SuppressWarnings("unchecked")
    protected static final BindVoidEventBus<TunnelUnaliveListener, Tunnel> ON_UNALIVE =
            EventBuses.of(TunnelUnaliveListener.class, TunnelUnaliveListener::onUnalive);

    @SuppressWarnings("unchecked")
    protected static final BindVoidEventBus<TunnelCloseListener, Tunnel> ON_CLOSE =
            EventBuses.of(TunnelCloseListener.class, TunnelCloseListener::onClose);

    protected TunnelEvents() {
    }

}
