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
package com.tny.game.net.transport;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import org.slf4j.*;

import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.transport.listener.TunnelEventBuses.*;

/**
 * 抽象通道
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class AbstractNetTunnel<E extends NetEndpoint> extends AbstractConnector implements NetTunnel {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractNetTunnel.class);

    private volatile TunnelStatus status = TunnelStatus.INIT;

    /*管道 id*/
    private final long id;

    /*访问 id*/
    private long accessId;

    /* 管道模式 */
    private final NetAccessMode accessMode;

    /* 会话终端 */
    protected volatile E endpoint;

    /* 上下文 */
    private final NetworkContext context;

    /* endpoint 锁 */
    private final StampedLock endpointLock = new StampedLock();

    protected AbstractNetTunnel(long id, NetAccessMode accessMode, NetworkContext context) {
        this.id = id;
        this.accessMode = accessMode;
        this.context = context;
    }

    @Override
    public long getAccessId() {
        return this.accessId;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public NetAccessMode getAccessMode() {
        return this.accessMode;
    }

    @Override
    public boolean isClosed() {
        return this.status == TunnelStatus.CLOSED;
    }

    @Override
    public boolean isOpen() {
        return this.status == TunnelStatus.OPEN;
    }

    @Override
    public TunnelStatus getStatus() {
        return this.status;
    }

    @Override
    public Certificate getCertificate() {
        return this.endpoint.getCertificate();
    }

    @Override
    public void setAccessId(long accessId) {
        this.accessId = accessId;
    }

    @Override
    public void ping() {
        this.write(TickMessage.ping(), null);
    }

    @Override
    public void pong() {
        this.write(TickMessage.pong(), null);
    }

    @Override
    public NetEndpoint getEndpoint() {
        return this.endpoint;
    }

    protected void setEndpoint(E endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public NetworkContext getContext() {
        return this.context;
    }

    @Override
    public boolean receive(NetMessage message) {
        return StampedLockAide.supplyInOptimisticReadLock(this.endpointLock, this::doReceive, message);
    }

    private boolean doReceive(NetMessage message) {
        E endpoint = this.endpoint;
        var rpcContext = RpcTransactionContext.createEnter(this, message, true);
        var rpcMonitor = this.context.getRpcMonitor();
        rpcMonitor.onReceive(rpcContext);
        while (true) {
            if (endpoint.isClosed()) {
                return false;
            }
            if (endpoint.receive(rpcContext)) {
                return true;
            }
        }
    }

    @Override
    public SendReceipt send(MessageContent content) {
        return StampedLockAide.supplyInOptimisticReadLock(this.endpointLock, () -> doSend(content));
    }

    private SendReceipt doSend(MessageContent messageContext) {
        return this.endpoint.send(this, messageContext);
    }

    @Override
    public boolean bind(NetEndpoint endpoint) {
        if (endpoint == null) {
            return false;
        }
        if (this.endpoint == endpoint) {
            return true;
        }
        synchronized (this) {
            if (this.endpoint == endpoint) {
                return true;
            }
            if (this.endpoint == null) {
                this.endpoint = as(endpoint);
                return true;
            } else {
                Certificate certificate = endpoint.getCertificate();
                if (!certificate.isAuthenticated()) {
                    return false;
                }
                return StampedLockAide.supplyInWriteLock(this.endpointLock, () -> replaceEndpoint(endpoint));
            }
        }
    }

    protected abstract boolean replaceEndpoint(NetEndpoint endpoint);

    @Override
    public boolean open() {
        if (this.isClosed()) {
            return false;
        }
        if (this.isActive()) {
            return true;
        }
        synchronized (this) {
            if (this.isClosed()) {
                return false;
            }
            if (this.isActive()) {
                return true;
            }
            if (!this.onOpen()) {
                return false;
            }
            this.status = TunnelStatus.OPEN;
            this.onOpened();
        }
        buses().activateEvent().notify(this);
        return true;
    }

    @Override
    public void disconnect() {
        NetEndpoint endpoint;
        synchronized (this) {
            if (this.status == TunnelStatus.CLOSED || this.status == TunnelStatus.SUSPEND) {
                return;
            }
            this.doDisconnect();
            this.status = TunnelStatus.SUSPEND;
            endpoint = this.endpoint;
            this.onDisconnected();
        }
        if (endpoint != null) { // 避免死锁
            endpoint.onUnactivated(this);
        }
        buses().unactivatedEvent().notify(this);
    }

    @Override
    public boolean close() {
        if (this.status == TunnelStatus.CLOSED) {
            return false;
        }
        NetEndpoint endpoint;
        synchronized (this) {
            if (this.status == TunnelStatus.CLOSED) {
                return false;
            }
            this.status = TunnelStatus.CLOSED;
            this.onClose();
            this.doDisconnect();
            endpoint = this.endpoint;
            this.onClosed();
        }
        if (endpoint != null) { // 避免死锁
            endpoint.onUnactivated(this);
        }
        buses().closeEvent().notify(this);
        return true;
    }

    @Override
    public void reset() {
        if (this.status == TunnelStatus.INIT) {
            return;
        }
        synchronized (this) {
            if (this.status == TunnelStatus.INIT) {
                return;
            }
            if (!this.isActive()) {
                this.disconnect();
            }
            this.status = TunnelStatus.INIT;
        }
    }

    protected abstract void doDisconnect();

    protected abstract boolean onOpen();

    protected abstract void onOpened();

    protected abstract void onClose();

    protected abstract void onClosed();

    protected abstract void onDisconnected();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractNetTunnel<?> that)) {
            return false;
        }
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

}
