package com.tny.game.net.endpoint;


import com.tny.game.common.event.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.listener.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public abstract class AbstractEndpointKeeper<UID, E extends Endpoint<UID>, EK extends E> implements EndpointKeeper<UID, E> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    protected static final BindP1EventBus<EndpointKeeperListener, EndpointKeeper, Session> onAddSession =
            EventBuses.of(EndpointKeeperListener.class, EndpointKeeperListener::onAddEndpoint);

    protected static final BindP1EventBus<EndpointKeeperListener, EndpointKeeper, Session> onRemoveSession =
            EventBuses.of(EndpointKeeperListener.class, EndpointKeeperListener::onRemoveEndpoint);

    /* 所有 endpoint */
    protected final Map<UID, EK> endpointMap = new ConcurrentHashMap<>();

    private String userType;

    protected AbstractEndpointKeeper(String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
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
    public void send2User(UID userId, MessageContext<UID> context) {
        E endpoint = this.getEndpoint(userId);
        if (endpoint != null) {
            endpoint.send(context);
        }
    }

    @Override
    public void send2Users(Collection<UID> userIds, MessageContext<UID> context) {
        this.doSendMultiID(userIds.stream(), context);
    }

    @Override
    public void send2Users(Stream<UID> userIdsStream, MessageContext<UID> context) {
        this.doSendMultiID(userIdsStream, context);
    }

    @Override
    public void send2AllOnline(MessageContext<UID> context) {
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
        if (endpoint != null)
            endpoint.close();
        return endpoint;
    }

    @Override
    public void closeAll() {
        this.endpointMap.forEach((key, endpoint) -> endpoint.close());
    }

    @Override
    public E offline(UID userId) {
        E endpoint = this.endpointMap.get(userId);
        if (endpoint != null)
            endpoint.offline();
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
            if (endpoint.isOnline())
                online++;
        }
        return online;
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
            onAddSession.addListener(l);
        });
    }

    @Override
    public void removeListener(EndpointKeeperListener<UID> listener) {
        onAddSession.removeListener(listener);
        onRemoveSession.removeListener(listener);
    }

    private void doSendMultiID(Stream<UID> userIds, MessageContext<UID> context) {
        userIds.forEach(userId -> {
            E endpoint = this.getEndpoint(userId);
            if (endpoint != null) {
                endpoint.send(context);
            }
        });
    }

}
