package com.tny.game.net.transport;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.base.*;
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
public abstract class AbstractTunnel<UID, E extends NetEndpoint<UID>> extends AbstractCommunicator<UID> implements NetTunnel<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractTunnel.class);

    protected volatile TunnelStatus status = TunnelStatus.INIT;

    /*管道 id*/
    private final long id;

    /*访问 id*/
    private long accessId;

    /* 管道模式 */
    private final TunnelMode mode;

    /* 会话终端 */
    protected volatile E endpoint;

    private final NetBootstrapContext<UID> bootstrapContext;

    /* endpoint 锁 */
    private final StampedLock endpointLock = new StampedLock();

    protected AbstractTunnel(long id, TunnelMode mode, NetBootstrapContext<UID> bootstrapContext) {
        this.id = id;
        this.mode = mode;
        this.bootstrapContext = bootstrapContext;
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
    public UID getUserId() {
        return this.getCertificate().getUserId();
    }

    @Override
    public String getUserType() {
        return this.getCertificate().getUserType();
    }

    @Override
    public boolean isLogin() {
        return getCertificate().isAuthenticated();
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

    @Override
    public NetBootstrapContext<UID> getNetBootstrapContext() {
        return this.bootstrapContext;
    }

    @Override
    public boolean receive(Message message) {
        return StampedLockAide.supplyInOptimisticReadLock(this.endpointLock,
                () -> {
                    E endpoint = this.endpoint;
                    while (true) {
                        if (endpoint.isClosed()) {
                            return false;
                        }
                        if (endpoint.receive(this, message)) {
                            return true;
                        }
                    }
                });
    }

    @Override
    public SendContext send(MessageContext messageContext) {
        return StampedLockAide.supplyInOptimisticReadLock(this.endpointLock,
                () -> this.endpoint.send(this, messageContext));

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
                return StampedLockAide.supplyInWriteLock(this.endpointLock,
                        () -> bindEndpoint(endpoint));
            }
        }
    }

    protected abstract boolean bindEndpoint(NetEndpoint<UID> endpoint);

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
        NetEndpoint<UID> endpoint;
        synchronized (this) {
            if (this.status == TunnelStatus.CLOSED || this.status == TunnelStatus.SUSPEND) {
                return;
            }
            this.onDisconnect();
            this.status = TunnelStatus.SUSPEND;
            endpoint = this.endpoint;
        }
        buses().unactivatedEvent().notify(this);
        if (endpoint != null) {
            endpoint.onUnactivated(this);
        }
    }

    @Override
    public void close() {
        if (this.status == TunnelStatus.CLOSED) {
            return;
        }
        NetEndpoint<UID> endpoint;
        synchronized (this) {
            if (this.status == TunnelStatus.CLOSED) {
                return;
            }
            this.onClose();
            this.status = TunnelStatus.CLOSED;
            this.onClosed();
            endpoint = this.endpoint;
        }
        buses().closeEvent().notify(this);
        if (endpoint != null) {
            endpoint.onUnactivated(this);
        }
    }

    protected abstract boolean onOpen();

    protected abstract void onOpened();

    protected abstract void onClose();

    protected abstract void onClosed();

    protected abstract void onDisconnect();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractTunnel)) {
            return false;
        }
        AbstractTunnel<?, ?> that = (AbstractTunnel<?, ?>)o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

}
