package com.tny.game.net.transport.listener;

import com.tny.game.common.event.*;
import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-03 15:55
 */
public class TunnelEventBuses extends BaseEventBuses<TunnelListener> {

    // @SuppressWarnings("unchecked")
    // private final BindVoidEventBus<TunnelAuthenticateListener, Tunnel> ON_AUTHENTICATE =
    //         EventBuses.of(TunnelAuthenticateListener.class, TunnelAuthenticateListener::onAuthenticate);

    @SuppressWarnings("unchecked")
    private final BindVoidEventBus<TunnelActivateListener, Tunnel> ON_ACTIVATE =
            EventBuses.of(TunnelActivateListener.class, TunnelActivateListener::onActivate);

    @SuppressWarnings("unchecked")
    private final BindVoidEventBus<TunnelUnactivatedListener, Tunnel> ON_UNACTIVATED =
            EventBuses.of(TunnelUnactivatedListener.class, TunnelUnactivatedListener::onUnactivated);

    @SuppressWarnings("unchecked")
    private final BindVoidEventBus<TunnelCloseListener, Tunnel> ON_CLOSE =
            EventBuses.of(TunnelCloseListener.class, TunnelCloseListener::onClose);

    private final static TunnelEventBuses eventBuses = new TunnelEventBuses();

    private TunnelEventBuses() {
        super();
    }

    public static TunnelEventBuses buses() {
        return eventBuses;
    }

    // public BindVoidEventBus<TunnelAuthenticateListener, Tunnel> authenticateEvent() {
    //     return ON_AUTHENTICATE;
    // }

    public BindVoidEventBus<TunnelActivateListener, Tunnel> activateEvent() {
        return ON_ACTIVATE;
    }

    public BindVoidEventBus<TunnelUnactivatedListener, Tunnel> unactivatedEvent() {
        return ON_UNACTIVATED;
    }

    public BindVoidEventBus<TunnelCloseListener, Tunnel> closeEvent() {
        return ON_CLOSE;
    }
}
