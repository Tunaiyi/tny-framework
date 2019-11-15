package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import org.slf4j.*;

import java.util.Objects;
import java.util.concurrent.locks.*;

import static com.tny.game.net.transport.listener.TunnelEventBuses.*;

/**
 * 抽象通道
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class AbstractNetTunnel<UID, E extends NetEndpoint<UID>> extends AbstractNetter<UID> implements NetTunnel<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractNetTunnel.class);

    protected volatile TunnelState state = TunnelState.INIT;

    private final long id;

    private long accessId;

    private TunnelMode mode;

    private MessageFactory<UID> messageFactory;

    protected volatile E endpoint;

    protected Lock lock = new ReentrantLock();

    protected AbstractNetTunnel(TunnelMode mode, E endpoint, MessageFactory<UID> messageFactory) {
        this.id = NetAide.newTunnelId();
        this.mode = mode;
        this.messageFactory = messageFactory;
        this.endpoint = endpoint;
    }

    @Override
    public long getAccessId() {
        return accessId;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public TunnelMode getMode() {
        return mode;
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
        return getCertificate().isAutherized();
    }

    @Override
    public boolean isClosed() {
        return state == TunnelState.CLOSE;
    }

    @Override
    public boolean isAlive() {
        return state == TunnelState.ACTIVATE;
    }

    @Override
    public TunnelState getState() {
        return state;
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
        this.write(DetectMessage.ping(), null);
    }

    @Override
    public void pong() {
        this.write(DetectMessage.pong(), null);
    }

    @Override
    public NetEndpoint<UID> getEndpoint() {
        return this.endpoint;
    }

    @Override
    public MessageFactory<UID> getMessageFactory() {
        return this.messageFactory;
    }

    @Override
    public boolean receive(Message<UID> message) {
        return endpoint.receive(this, message);
    }

    @Override
    public SendContext<UID> send(MessageContext<UID> messageContext) {
        return endpoint.send(this, messageContext);
    }

    @Override
    public boolean open() {
        if (this.isClosed())
            return false;
        if (this.isAvailable())
            return true;
        lock.lock();
        try {
            if (this.isClosed())
                return false;
            if (this.isAvailable())
                return true;
            if (!this.onOpen())
                return false;
            this.state = TunnelState.ACTIVATE;
        } finally {
            lock.unlock();
        }
        buses().activateEvent().notify(this);
        return true;
    }

    @Override
    public void disconnect() {
        lock.lock();
        try {
            if (state == TunnelState.CLOSE || state == TunnelState.UNACTIVATED)
                return;
            this.doDisconnect();
            this.state = TunnelState.UNACTIVATED;
        } finally {
            lock.unlock();
        }
        buses().unactivatedEvent().notify(this);
        NetEndpoint<UID> endpoint = this.endpoint;
        if (endpoint != null)
            endpoint.onUnactivated(this);
    }

    @Override
    public void close() {
        if (state == TunnelState.CLOSE)
            return;
        lock.lock();
        try {
            if (state == TunnelState.CLOSE)
                return;
            this.onClose();
            this.state = TunnelState.CLOSE;
        } finally {
            lock.unlock();
        }
        buses().closeEvent().notify(this);
        NetEndpoint<UID> endpoint = this.endpoint;
        if (endpoint != null)
            endpoint.onUnactivated(this);
    }

    protected abstract boolean onOpen();

    protected abstract void onClose();

    protected abstract void doDisconnect();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AbstractNetTunnel))
            return false;
        AbstractNetTunnel<?, ?> that = (AbstractNetTunnel<?, ?>) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
