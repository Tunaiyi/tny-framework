package com.tny.game.net.endpoint;

import com.tny.game.common.event.bus.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.listener.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.tny.game.common.utils.ObjectAide.*;

@SuppressWarnings("unchecked")
public abstract class AbstractEndpointKeeper<UID, E extends Endpoint<UID>, NE extends E> implements NetEndpointKeeper<UID, E> {

	private static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

	@SuppressWarnings("rawtypes")
	private final BindP1EventBus<EndpointKeeperListener, EndpointKeeper, Endpoint> onAddSession =
			EventBuses.of(EndpointKeeperListener.class, EndpointKeeperListener::onAddEndpoint);

	@SuppressWarnings("rawtypes")
	private final BindP1EventBus<EndpointKeeperListener, EndpointKeeper, Endpoint> onRemoveSession =
			EventBuses.of(EndpointKeeperListener.class, EndpointKeeperListener::onRemoveEndpoint);

	/* 所有 endpoint */
	private final String userType;

	private final Map<UID, NE> endpointMap = new ConcurrentHashMap<>();

	protected AbstractEndpointKeeper(String userType) {
		this.userType = userType;
	}

	protected void onEndpointOnline(E endpoint) {

	}

	protected void onEndpointOffline(E endpoint) {

	}

	protected void onEndpointClose(E endpoint) {

	}

	@Override
	public void notifyEndpointOnline(Endpoint<?> endpoint) {
		if (!this.getUserType().equals(endpoint.getUserType())) {
			return;
		}
		this.onEndpointOnline((E)endpoint);
	}

	@Override
	public void notifyEndpointOffline(Endpoint<?> endpoint) {
		if (!this.getUserType().equals(endpoint.getUserType())) {
			return;
		}
		this.onEndpointOffline((E)endpoint);
	}

	@Override
	public void notifyEndpointClose(Endpoint<?> endpoint) {
		if (!this.getUserType().equals(endpoint.getUserType())) {
			return;
		}
		NE netSession = as(endpoint);
		if (this.removeEndpoint(netSession.getUserId(), netSession)) {
			onEndpointClose((E)endpoint);
		}
	}

	@Override
	public String getUserType() {
		return this.userType;
	}

	@Override
	public E getEndpoint(UID userId) {
		return this.endpointMap.get(userId);
	}

	@Override
	public Map<UID, E> getAllEndpoints() {
		return Collections.unmodifiableMap(this.endpointMap);
	}

	@Override
	public void send2User(UID userId, MessageContext context) {
		E endpoint = this.getEndpoint(userId);
		if (endpoint != null) {
			endpoint.send(context);
		}
	}

	@Override
	public void send2Users(Collection<UID> userIds, MessageContext context) {
		this.doSendMultiId(userIds.stream(), context);
	}

	@Override
	public void send2Users(Stream<UID> userIdsStream, MessageContext context) {
		this.doSendMultiId(userIdsStream, context);
	}

	@Override
	public void send2AllOnline(MessageContext context) {
		for (E endpoint : this.endpointMap.values())
			endpoint.send(context);
	}

	@Override
	public int size() {
		return this.endpointMap.size();
	}

	@Override
	public E close(UID userId) {
		E endpoint = this.endpointMap.get(userId);
		if (endpoint != null) {
			endpoint.close();
		}
		return endpoint;
	}

	@Override
	public void closeAll() {
		this.endpointMap.forEach((key, endpoint) -> endpoint.close());
	}

	@Override
	public E offline(UID userId) {
		E endpoint = this.endpointMap.get(userId);
		if (endpoint != null) {
			endpoint.offline();
		}
		return endpoint;
	}

	@Override
	public void offlineAll() {
		this.endpointMap.forEach((key, session) -> session.offline());
	}

	@Override
	public boolean isOnline(UID userId) {
		E endpoint = this.getEndpoint(userId);
		return endpoint != null && endpoint.isOnline();
	}

	@Override
	public int countOnlineSize() {
		int online = 0;
		for (E endpoint : this.endpointMap.values()) {
			if (endpoint.isOnline()) {
				online++;
			}
		}
		return online;
	}

	protected NE findEndpoint(UID uid) {
		return this.endpointMap.get(uid);
	}

	protected boolean removeEndpoint(UID uid, NE existOne) {
		if (this.endpointMap.remove(uid, existOne)) {
			onRemoveSession.notify(this, existOne);
			return true;
		}
		return false;
	}

	protected NE replaceEndpoint(UID uid, NE newOne) {
		NE oldOne = this.endpointMap.put(uid, newOne);
		if (oldOne != null && oldOne != newOne) {
			oldOne.close();
			onRemoveSession.notify(this, oldOne);
		}
		onAddSession.notify(this, newOne);
		return oldOne;
	}

	@Override
	public void addListener(EndpointKeeperListener<UID> listener) {
		onAddSession.addListener(listener);
		onRemoveSession.addListener(listener);
	}

	@Override
	public void addListener(Collection<EndpointKeeperListener<UID>> listeners) {
		listeners.forEach(l -> {
			onAddSession.addListener(l);
			onRemoveSession.addListener(l);
		});
	}

	@Override
	public void removeListener(EndpointKeeperListener<UID> listener) {
		onAddSession.removeListener(listener);
		onRemoveSession.removeListener(listener);
	}

	private void doSendMultiId(Stream<UID> userIds, MessageContext context) {
		userIds.forEach(userId -> {
			E endpoint = this.getEndpoint(userId);
			if (endpoint != null) {
				endpoint.send(context);
			}
		});
	}

	protected void monitorEndpoint() {
		int size = 0;
		int online = 0;
		for (NE session : this.endpointMap.values()) {
			size++;
			if (session.isOnline()) {
				online++;
			}
		}
		LOG.info("会话管理器#{} Group -> 会话数量为 {} | 在线数 {}", this.getUserType(), size, online);
	}

}
