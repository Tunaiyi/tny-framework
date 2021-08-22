package com.tny.game.net.endpoint.listener;

import com.tny.game.common.event.bus.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;

/**
 * <p>
 */
public class EndpointEventBuses extends BaseEventBuses<EndpointListener> {

	// @SuppressWarnings("unchecked")
	// private final BindP1EventBus<EndpointAcceptListener, Endpoint, Tunnel> ON_ACCEPT =
	//         EventBuses.of(EndpointAcceptListener.class, EndpointAcceptListener::onAccept);

	@SuppressWarnings("unchecked")
	private final BindVoidEventBus<EndpointOnlineListener, Endpoint> ON_ONLINE =
			EventBuses.of(EndpointOnlineListener.class, EndpointOnlineListener::onOnline);

	@SuppressWarnings("unchecked")
	private final BindVoidEventBus<EndpointOfflineListener, Endpoint> ON_OFFLINE =
			EventBuses.of(EndpointOfflineListener.class, EndpointOfflineListener::onOffline);

	@SuppressWarnings("unchecked")
	private final BindVoidEventBus<EndpointCloseListener, Endpoint> ON_CLOSE =
			EventBuses.of(EndpointCloseListener.class, EndpointCloseListener::onClose);

	private final static EndpointEventBuses eventBuses = new EndpointEventBuses();

	private EndpointEventBuses() {
		super();
	}

	public static EndpointEventBuses buses() {
		return eventBuses;
	}

	// public BindP1EventBus<EndpointAcceptListener, Endpoint, Tunnel> acceptEvent() {
	//     return ON_ACCEPT;
	// }

	public BindVoidEventBus<EndpointOnlineListener, Endpoint> onlineEvent() {
		return this.ON_ONLINE;
	}

	public BindVoidEventBus<EndpointOfflineListener, Endpoint> offlineEvent() {
		return this.ON_OFFLINE;
	}

	public BindVoidEventBus<EndpointCloseListener, Endpoint> closeEvent() {
		return this.ON_CLOSE;
	}

}
