package com.tny.game.net.endpoint.listener;

import com.tny.game.common.event.bus.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ClientEventBuses extends BaseEventBuses<ClientListener> {

    private final BindP1EventBus<ClientActivateListener, Client, Tunnel> ON_ACTIVATE =
            EventBuses.of(ClientActivateListener.class, ClientActivateListener::onActivate);

    private final BindP1EventBus<ClientUnactivatedListener, Client, Tunnel> ON_UNACTIVATED =
            EventBuses.of(ClientUnactivatedListener.class, ClientUnactivatedListener::onUnactivated);

    private final BindVoidEventBus<ClientOpenListener, Client> ON_OPEN =
            EventBuses.of(ClientOpenListener.class, ClientOpenListener::onOpen);

    private final BindVoidEventBus<ClientCloseListener, Client> ON_CLOSE =
            EventBuses.of(ClientCloseListener.class, ClientCloseListener::onClose);

    private ClientEventBuses() {
    }

    private final static ClientEventBuses eventBuses = new ClientEventBuses();

    public static ClientEventBuses buses() {
        return eventBuses;
    }

    public <T> BindP1EventBus<ClientActivateListener<T>, Client<T>, Tunnel<T>> activateEvent() {
        return as(this.ON_ACTIVATE);
    }

    public <T> BindP1EventBus<ClientUnactivatedListener<T>, Client<T>, Tunnel<T>> unactivatedEvent() {
        return as(this.ON_UNACTIVATED);
    }

    public <T> BindVoidEventBus<ClientOpenListener<T>, Client<T>> openEvent() {
        return as(this.ON_OPEN);
    }

    public <T> BindVoidEventBus<ClientCloseListener<T>, Client<T>> closeEvent() {
        return as(this.ON_CLOSE);
    }

}
