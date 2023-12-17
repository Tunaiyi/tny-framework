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
public abstract class AbstractEndpointKeeper<E extends Endpoint, NE extends E> implements NetEndpointKeeper<E> {

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    @SuppressWarnings("rawtypes")
    private final BindP1EventBus<EndpointKeeperListener, EndpointKeeper, Endpoint> onAddSession =
            EventBuses.of(EndpointKeeperListener.class, EndpointKeeperListener::onAddEndpoint);

    @SuppressWarnings("rawtypes")
    private final BindP1EventBus<EndpointKeeperListener, EndpointKeeper, Endpoint> onRemoveSession =
            EventBuses.of(EndpointKeeperListener.class, EndpointKeeperListener::onRemoveEndpoint);

    /* 所有 endpoint */
    private final ContactType contactType;

    private final Map<Long, NE> endpointMap = new ConcurrentHashMap<>();

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
    public void notifyEndpointOnline(Endpoint endpoint) {
        if (!Objects.equals(this.getContactType(), endpoint.getContactType())) {
            return;
        }
        this.onEndpointOnline((E) endpoint);
    }

    @Override
    public void notifyEndpointOffline(Endpoint endpoint) {
        if (!Objects.equals(this.getContactType(), endpoint.getContactType())) {
            return;
        }
        this.onEndpointOffline((E) endpoint);
    }

    @Override
    public void notifyEndpointClose(Endpoint endpoint) {
        if (!Objects.equals(this.getContactType(), endpoint.getContactType())) {
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
    public E getEndpoint(long identify) {
        return this.endpointMap.get(identify);
    }

    @Override
    public Map<Long, E> getAllEndpoints() {
        return Collections.unmodifiableMap(this.endpointMap);
    }

    @Override
    public void send2User(long identify, MessageContent context) {
        E endpoint = this.getEndpoint(identify);
        if (endpoint != null) {
            endpoint.send(context);
        }
    }

    @Override
    public void send2Users(Collection<Long> identifies, MessageContent context) {
        this.doSendMultiId(identifies.stream(), context);
    }

    @Override
    public void send2Users(Stream<Long> identifiesStream, MessageContent context) {
        this.doSendMultiId(identifiesStream, context);
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
    public E close(long identify) {
        E endpoint = this.endpointMap.get(identify);
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
    public E offline(long identify) {
        E endpoint = this.endpointMap.get(identify);
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
    public boolean isOnline(long identify) {
        E endpoint = this.getEndpoint(identify);
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

    protected NE findEndpoint(long identify) {
        return this.endpointMap.get(identify);
    }

    protected boolean removeEndpoint(long identify, NE existOne) {
        if (this.endpointMap.remove(identify, existOne)) {
            onRemoveSession.notify(this, existOne);
            return true;
        }
        return false;
    }

    protected void replaceEndpoint(long identify, NE newOne) {
        NE oldOne = this.endpointMap.put(identify, newOne);
        if (oldOne != null && oldOne != newOne) {
            oldOne.close();
            onRemoveSession.notify(this, oldOne);
        }
        onAddSession.notify(this, newOne);
    }

    @Override
    public void addListener(EndpointKeeperListener listener) {
        onAddSession.addListener(listener);
        onRemoveSession.addListener(listener);
    }

    @Override
    public void addListener(Collection<EndpointKeeperListener> listeners) {
        listeners.forEach(l -> {
            onAddSession.addListener(l);
            onRemoveSession.addListener(l);
        });
    }

    @Override
    public void removeListener(EndpointKeeperListener listener) {
        onAddSession.removeListener(listener);
        onRemoveSession.removeListener(listener);
    }

    private void doSendMultiId(Stream<Long> identifies, MessageContent context) {
        identifies.forEach(identify -> {
            E endpoint = this.getEndpoint(identify);
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
