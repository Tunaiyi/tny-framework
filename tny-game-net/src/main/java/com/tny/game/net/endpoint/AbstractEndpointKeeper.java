/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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
public abstract class AbstractEndpointKeeper<I, E extends Endpoint<I>, NE extends E> implements NetEndpointKeeper<I, E> {

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    @SuppressWarnings("rawtypes")
    private final BindP1EventBus<EndpointKeeperListener, EndpointKeeper, Endpoint> onAddSession =
            EventBuses.of(EndpointKeeperListener.class, EndpointKeeperListener::onAddEndpoint);

    @SuppressWarnings("rawtypes")
    private final BindP1EventBus<EndpointKeeperListener, EndpointKeeper, Endpoint> onRemoveSession =
            EventBuses.of(EndpointKeeperListener.class, EndpointKeeperListener::onRemoveEndpoint);

    /* 所有 endpoint */
    private final ContactType contactType;

    private final Map<I, NE> endpointMap = new ConcurrentHashMap<>();

    protected AbstractEndpointKeeper(ContactType contactType) {
        this.contactType = contactType;
    }

    protected void onEndpointOnline(E endpoint) {

    }

    protected void onEndpointOffline(E endpoint) {

    }

    protected void onEndpointClose(E endpoint) {

    }

    @Override
    public void notifyEndpointOnline(Endpoint<?> endpoint) {
        if (!Objects.equals(this.getContactType(), endpoint.contactType())) {
            return;
        }
        this.onEndpointOnline((E) endpoint);
    }

    @Override
    public void notifyEndpointOffline(Endpoint<?> endpoint) {
        if (!Objects.equals(this.getContactType(), endpoint.contactType())) {
            return;
        }
        this.onEndpointOffline((E) endpoint);
    }

    @Override
    public void notifyEndpointClose(Endpoint<?> endpoint) {
        if (!Objects.equals(this.getContactType(), endpoint.contactType())) {
            return;
        }
        NE netSession = as(endpoint);
        if (this.removeEndpoint(netSession.getIdentify(), netSession)) {
            onEndpointClose((E) endpoint);
        }
    }

    @Override
    public ContactType getContactType() {
        return this.contactType;
    }

    @Override
    public E getEndpoint(I identify) {
        return this.endpointMap.get(identify);
    }

    @Override
    public Map<I, E> getAllEndpoints() {
        return Collections.unmodifiableMap(this.endpointMap);
    }

    @Override
    public void send2User(I userId, MessageContent context) {
        E endpoint = this.getEndpoint(userId);
        if (endpoint != null) {
            endpoint.send(context);
        }
    }

    @Override
    public void send2Users(Collection<I> userIds, MessageContent context) {
        this.doSendMultiId(userIds.stream(), context);
    }

    @Override
    public void send2Users(Stream<I> userIdsStream, MessageContent context) {
        this.doSendMultiId(userIdsStream, context);
    }

    @Override
    public void send2AllOnline(MessageContent context) {
        for (E endpoint : this.endpointMap.values())
            endpoint.send(context);
    }

    @Override
    public int size() {
        return this.endpointMap.size();
    }

    @Override
    public E close(I userId) {
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
    public E offline(I userId) {
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
    public boolean isOnline(I userId) {
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

    protected NE findEndpoint(I i) {
        return this.endpointMap.get(i);
    }

    protected boolean removeEndpoint(I i, NE existOne) {
        if (this.endpointMap.remove(i, existOne)) {
            onRemoveSession.notify(this, existOne);
            return true;
        }
        return false;
    }

    protected NE replaceEndpoint(I i, NE newOne) {
        NE oldOne = this.endpointMap.put(i, newOne);
        if (oldOne != null && oldOne != newOne) {
            oldOne.close();
            onRemoveSession.notify(this, oldOne);
        }
        onAddSession.notify(this, newOne);
        return oldOne;
    }

    @Override
    public void addListener(EndpointKeeperListener<I> listener) {
        onAddSession.addListener(listener);
        onRemoveSession.addListener(listener);
    }

    @Override
    public void addListener(Collection<EndpointKeeperListener<I>> listeners) {
        listeners.forEach(l -> {
            onAddSession.addListener(l);
            onRemoveSession.addListener(l);
        });
    }

    @Override
    public void removeListener(EndpointKeeperListener<I> listener) {
        onAddSession.removeListener(listener);
        onRemoveSession.removeListener(listener);
    }

    private void doSendMultiId(Stream<I> userIds, MessageContent context) {
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
        LOG.info("会话管理器#{} Group -> 会话数量为 {} | 在线数 {}", this.getContactType(), size, online);
    }

}
