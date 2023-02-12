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
import com.tny.game.net.base.*;
import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:09 下午
 */
class NettyRelayLinkConnector implements RelayConnectCallback {

    private String linkKey;

    private final ClientRelayContext relayContext;

    private final NetRemoteServeInstance instance;

    private final NettyServeInstanceConnectMonitor connector;

    private volatile RelayConnectorStatus status = RelayConnectorStatus.INIT;

    private volatile ClientRelayLink link;

    private int times;

    private final String username;

    private final static long[] delayTimeList = {1, 2, 2, 3, 3, 3, 5, 5, 5, 5, 10, 10, 10, 10, 10, 15};

    NettyRelayLinkConnector(ClientRelayContext relayContext, NetRemoteServeInstance instance, NettyServeInstanceConnectMonitor connector) {
        this.relayContext = relayContext;
        this.instance = instance;
        this.connector = connector;
        this.username = instance.username(relayContext.getAppServeName());
    }

    public URL getUrl() {
        return instance.url();
    }

    public synchronized void connect() {
        if (!status.isCanConnect()) {
            return;
        }
        if (link == null || !link.isActive()) {
            status = RelayConnectorStatus.CONNECTING;
            connector.connect(this);
        }
    }

    private void onConnected(RelayTransporter transporter) {
        if (status != RelayConnectorStatus.CONNECTING) {
            transporter.close();
            return;
        }
        this.status = RelayConnectorStatus.OPEN;
        this.times = 0;
        this.linkKey = relayContext.createLinkKey(username);
        this.link = new CommonClientRelayLink(this.linkKey, instance, transporter);
        this.link.auth(RpcServiceTypes.checkAppType(relayContext.getAppType()), username, relayContext.getAppInstanceId());
        transporter.addCloseListener(this::onClose);
    }

    private void onReconnected() {
        if (status.isCanConnect()) {
            connector.connect(this, delayTimeList[this.times % delayTimeList.length] * 1000);
            this.times++;
        }
    }

    private void onClose(RelayTransporter transporter) {
        if (status != RelayConnectorStatus.CLOSE) {
            this.status = RelayConnectorStatus.DISCONNECT;
            onReconnected();
        }
    }

    public synchronized void close() {
        status = RelayConnectorStatus.CLOSE;
    }

    @Override
    public void complete(boolean result, URL url, RelayTransporter transporter, Throwable cause) {
        if (this.status == RelayConnectorStatus.CLOSE) {
            NettyRemoteServeInstance.LOGGER.warn("Server [{}-{}-{}] Connector is closed",
                    instance.getServeName(), instance.getId(), this.linkKey);
            transporter.close();
            return;
        }
        if (result && transporter.isActive()) {
            NettyRemoteServeInstance.LOGGER.info("Server [{}-{}-{}] connect to {} success on the {}th times",
                    instance.getServeName(), instance.getId(), this.linkKey, url, times);
            onConnected(transporter);
        } else {
            this.status = RelayConnectorStatus.DISCONNECT;
            NettyRemoteServeInstance.LOGGER.warn("Server [{}-{}-{}] connect to {} failed {} times",
                    instance.getServeName(), instance.getId(), this.linkKey, url, times, cause.getCause());
            onReconnected();
        }
    }

}
