package com.tny.game.net.endpoint.listener;

import com.tny.game.common.event.*;
import com.tny.game.net.base.BaseEventBuses;
import com.tny.game.net.endpoint.Client;
import com.tny.game.net.transport.Tunnel;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-06 10:24
 */
public class ClientEventBuses extends BaseEventBuses<ClientListener> {

    @SuppressWarnings("unchecked")
    private final BindP1EventBus<ClientActivateListener, Client, Tunnel> ON_ACTIVATE =
            EventBuses.of(ClientActivateListener.class, ClientActivateListener::onActivate);

    @SuppressWarnings("unchecked")
    private final BindP1EventBus<ClientUnactivatedListener, Client, Tunnel> ON_UNACTIVATED =
            EventBuses.of(ClientUnactivatedListener.class, ClientUnactivatedListener::onUnactivated);

    @SuppressWarnings("unchecked")
    private final BindVoidEventBus<ClientOpenListener, Client> ON_OPEN =
            EventBuses.of(ClientOpenListener.class, ClientOpenListener::onOpen);

    @SuppressWarnings("unchecked")
    private final BindVoidEventBus<ClientCloseListener, Client> ON_CLOSE =
            EventBuses.of(ClientCloseListener.class, ClientCloseListener::onClose);

    private ClientEventBuses() {
    }

    private final static ClientEventBuses eventBuses = new ClientEventBuses();

    public static ClientEventBuses buses() {
        return eventBuses;
    }

    public BindP1EventBus<ClientActivateListener, Client, Tunnel> activateEvent() {
        return ON_ACTIVATE;
    }

    public BindP1EventBus<ClientUnactivatedListener, Client, Tunnel> unactivatedEvent() {
        return ON_UNACTIVATED;
    }

    public BindVoidEventBus<ClientOpenListener, Client> openEvent() {
        return ON_OPEN;
    }

    public BindVoidEventBus<ClientCloseListener, Client> closeEvent() {
        return ON_CLOSE;
    }


}
