package com.tny.game.net.endpoint.listener;

import com.tny.game.common.event.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

/**
 * <p>
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
