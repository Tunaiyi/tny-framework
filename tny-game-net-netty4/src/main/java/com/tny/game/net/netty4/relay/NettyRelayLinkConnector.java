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
package com.tny.game.net.netty4.relay;

import com.tny.game.common.url.*;
import com.tny.game.net.application.*;
import com.tny.game.net.relay.link.*;

import java.util.concurrent.locks.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:09 下午
 */
class NettyRelayLinkConnector implements RelayConnectCallback {

    private String linkKey;

    private final ClientRelayContext relayContext;

    private final NetRelayServeInstance instance;

    private final NettyServeInstanceConnectMonitor connector;

    private volatile RelayConnectionStatus status = RelayConnectionStatus.INIT;

    private volatile ClientRelayLink link;

    private int times;

    private final String username;

    private Lock statusLock = new ReentrantLock();

    private final static long[] delayTimeList = {1, 2, 2, 3, 3, 3, 5, 5, 5, 5, 10, 10, 10, 10, 10, 15};

    NettyRelayLinkConnector(ClientRelayContext relayContext, NetRelayServeInstance instance, NettyServeInstanceConnectMonitor connector) {
        this.relayContext = relayContext;
        this.instance = instance;
        this.connector = connector;
        this.username = instance.username(relayContext.getService());
    }

    public URL getUrl() {
        return instance.url();
    }

    public void connect() {
        statusLock.lock();
        try {
            if (!status.isCanConnect()) {
                return;
            }
            if (link == null || !link.isActive()) {
                status = RelayConnectionStatus.CONNECTING;
                connector.connect(this);
            }
        } finally {
            statusLock.unlock();
        }
    }

    private void onConnected(RelayTransport transport) {
        if (status != RelayConnectionStatus.CONNECTING) {
            transport.close();
            return;
        }
        this.status = RelayConnectionStatus.OPEN;
        this.times = 0;
        this.linkKey = relayContext.createLinkKey(username);
        this.link = new CommonClientRelayLink(this.linkKey, instance, transport);
        this.link.auth(RpcServiceTypes.checkService(username), username, relayContext.getInstanceId());
        transport.addCloseListener(this::onClose);
    }

    private void onReconnected() {
        if (status.isCanConnect()) {
            connector.connect(this, delayTimeList[this.times % delayTimeList.length] * 1000);
            this.times++;
        }
    }

    private void onClose(RelayTransport transport) {
        if (status != RelayConnectionStatus.CLOSE) {
            this.status = RelayConnectionStatus.DISCONNECT;
            onReconnected();
        }
    }

    public void close() {
        statusLock.lock();
        try {
            status = RelayConnectionStatus.CLOSE;
        } finally {
            statusLock.unlock();
        }
    }

    @Override
    public void complete(boolean result, URL url, RelayTransport transport, Throwable cause) {
        if (this.status == RelayConnectionStatus.CLOSE) {
            NettyRelayServeInstance.LOGGER.warn("Server [{}-{}-{}] Connector is closed",
                    instance.getServeName(), instance.getId(), this.linkKey);
            transport.close();
            return;
        }
        if (result && transport.isActive()) {
            NettyRelayServeInstance.LOGGER.info("Server [{}-{}-{}] connect to {} success on the {}th times",
                    instance.getServeName(), instance.getId(), this.linkKey, url, times);
            onConnected(transport);
        } else {
            this.status = RelayConnectionStatus.DISCONNECT;
            NettyRelayServeInstance.LOGGER.warn("Server [{}-{}-{}] connect to {} failed {} times",
                    instance.getServeName(), instance.getId(), this.linkKey, url, times, cause.getCause());
            onReconnected();
        }
    }

}
