package com.tny.game.net.transport;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import org.slf4j.*;

import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.transport.listener.TunnelEventBuses.*;

/**
 * 抽象通道
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class AbstractNetTunnel<UID, E extends NetEndpoint<UID>> extends AbstractCommunicator<UID> implements NetTunnel<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractNetTunnel.class);

    protected volatile TunnelStatus status = TunnelStatus.INIT;

    /*管道 id*/
    private final long id;

    /*访问 id*/
    private long accessId;

    /* 管道模式 */
    private final TunnelMode mode;

    /* 会话终端 */
    protected volatile E endpoint;

    /* 上下文 */
    private final NetworkContext context;

    /* endpoint 锁 */
    private final StampedLock endpointLock = new StampedLock();

    protected AbstractNetTunnel(long id, TunnelMode mode, NetworkContext context) {
        this.id = id;
        this.mode = mode;
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
    public TunnelMode getMode() {
        return this.mode;
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
    public Certificate<UID> getCertificate() {
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
    public NetEndpoint<UID> getEndpoint() {
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
    public boolean receive(Message message) {
        return StampedLockAide.supplyInOptimisticReadLock(this.endpointLock, () -> doReceive(message));
    }

    private boolean doReceive(Message message) {
        E endpoint = this.endpoint;
        while (true) {
            if (endpoint.isClosed()) {
                return false;
            }
            if (endpoint.receive(this, message)) {
                return true;
            }
        }
    }

    @Override
    public SendReceipt send(MessageContext messageContext) {
        return StampedLockAide.supplyInOptimisticReadLock(this.endpointLock,
                () -> doSend(messageContext));
    }

    private SendReceipt doSend(MessageContext messageContext) {
        return this.endpoint.send(this, messageContext);
    }

    @Override
    public boolean bind(NetEndpoint<UID> endpoint) {
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
                Certificate<UID> certificate = endpoint.getCertificate();
                if (!certificate.isAuthenticated()) {
                    return false;
                }
                return StampedLockAide.supplyInWriteLock(this.endpointLock,
                        () -> replaceEndpoint(endpoint));
            }
        }
    }

    protected abstract boolean replaceEndpoint(NetEndpoint<UID> endpoint);

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
            buses().activateEvent().notify(this);
            this.onOpened();
        }
        return true;
    }

    @Override
    public void disconnect() {
        NetEndpoint<UID> endpoint;
        synchronized (this) {
            if (this.status == TunnelStatus.CLOSED || this.status == TunnelStatus.SUSPEND) {
                return;
            }
            this.onDisconnect();
            this.doDisconnect();
            this.status = TunnelStatus.SUSPEND;
            endpoint = this.endpoint;
            buses().unactivatedEvent().notify(this);
            if (endpoint != null) {
                endpoint.onUnactivated(this);
            }
            this.onDisconnected();
        }
    }

    @Override
    public boolean close() {
        if (this.status == TunnelStatus.CLOSED) {
            return false;
        }
        NetEndpoint<UID> endpoint;
        synchronized (this) {
            if (this.status == TunnelStatus.CLOSED) {
                return false;
            }
            this.status = TunnelStatus.CLOSED;
            this.onClose();
            this.doDisconnect();
            endpoint = this.endpoint;
            buses().closeEvent().notify(this);
            if (endpoint != null) {
                endpoint.onUnactivated(this);
            }
            this.onClosed();
        }
        return true;
    }

    protected abstract void doDisconnect();

    protected abstract boolean onOpen();

    protected abstract void onOpened();

    protected abstract void onClose();

    protected abstract void onClosed();

    protected abstract void onDisconnect();

    protected abstract void onDisconnected();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractNetTunnel)) {
            return false;
        }
        AbstractNetTunnel<?, ?> that = (AbstractNetTunnel<?, ?>)o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

}
