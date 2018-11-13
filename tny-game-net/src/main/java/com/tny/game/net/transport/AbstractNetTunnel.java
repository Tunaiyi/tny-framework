package com.tny.game.net.transport;

import com.tny.game.common.context.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.locks.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.transport.listener.TunnelEventBuses.*;

/**
 * 抽象通道
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class AbstractNetTunnel<UID> extends AbstractNetter<UID> implements NetTunnel<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractNetTunnel.class);

    protected volatile TunnelState state = TunnelState.INIT;

    private final long id;

    private long accessId;

    private TunnelMode mode;

    /* 附加属性 */
    private Attributes attributes;

    /* 认证 */
    private Certificate<UID> certificate;

    private volatile NetEndpoint<UID> endpoint;

    private MessageFactory<UID> messageBuilderFactory;

    protected Lock lock = new ReentrantLock();

    protected AbstractNetTunnel(Certificate<UID> certificate, TunnelMode mode, MessageFactory<UID> messageBuilderFactory) {
        super(MessageIdCreator.TUNNEL_SENDER_MESSAGE_ID_MARK);
        this.mode = mode;
        this.certificate = certificate;
        this.id = NetAide.newTunnelId();
        this.messageBuilderFactory = messageBuilderFactory;
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
        return certificate.getUserId();
    }

    @Override
    public String getUserType() {
        return this.certificate.getUserType();
    }

    @Override
    public boolean isLogin() {
        return certificate.isAutherized();
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
        return certificate;
    }

    @Override
    public NetTunnel<UID> setAccessId(long accessId) {
        this.accessId = accessId;
        return this;
    }

    /**
     * 使用指定认证登陆
     *
     * @param certificate 指定认证
     */
    @Override
    public void authenticate(Certificate<UID> certificate) throws ValidatorFailException {
        if (!certificate.isAutherized())
            throw new ValidatorFailException("无效授权");
        if (this.certificate.isAutherized())
            throw new ValidatorFailException(NetResultCode.UNLOGIN, format("{} 已经授权", this));
        this.certificate = certificate;
        buses().authenticateEvent().notify(this);
    }

    @Override
    public boolean receive(Message<UID> message) {
        NetLogger.logReceive(this, message);
        MessageMode mode = message.getMode();
        switch (mode) {
            case PUSH:
            case REQUEST:
            case RESPONSE:
                try {
                    NetEndpoint<UID> endpoint = this.endpoint;
                    if (endpoint != null) {
                        if (endpoint.receive(this, message))
                            return true;
                        if (mode == MessageMode.REQUEST)
                            this.sendAsyn(MessageContexts.respond(message.getHeader(), NetResultCode.NO_RECEIVE_MODE, message.getId()));
                    }
                } finally {
                    if (mode == MessageMode.RESPONSE)
                        this.callbackFuture(message);
                }
                return true;
            default:
                break;
        }
        return true;
    }

    @Override
    public SendContext<UID> sendAsyn(MessageContext<UID> context) {
        try {
            NetEndpoint<UID> endpoint = this.endpoint;
            if (endpoint != null)
                return endpoint.sendAsyn(this, context);
            else
                return send(context, 0);
        } catch (Throwable e) {
            return ifNull(context, EmptySendContext.empty());
        }
    }

    @Override
    public SendContext<UID> sendSync(MessageContext<UID> context, long timeout) {
        NetEndpoint<UID> endpoint = this.endpoint;
        if (endpoint != null)
            return endpoint.sendSync(this, context, timeout);
        else
            return send(context, timeout);
    }

    protected SendContext<UID> send(MessageContext<UID> context, long waitForSendTimeout) throws NetException {
        Message<UID> message = this.createMessage(createMessageID(), context);
        return write(message, context, waitForSendTimeout, this);
    }

    @Override
    public Message<UID> createMessage(long messageId, MessageContext<UID> context) {
        return this.messageBuilderFactory.create(messageId, context, this.getCertificate());
    }

    @Override
    public void ping() {
        this.write(DetectMessage.ping(), null, 0, null);
    }

    @Override
    public void pong() {
        this.write(DetectMessage.pong(), null, 0, null);
    }

    @Override
    public Attributes attributes() {
        if (this.attributes != null)
            return this.attributes;
        synchronized (this) {
            if (this.attributes != null)
                return this.attributes;
            return this.attributes = ContextAttributes.create();
        }
    }

    @Override
    public Optional<Endpoint<UID>> getBindEndpoint() {
        return Optional.ofNullable(this.endpoint);
    }

    @Override
    public boolean isBind() {
        return this.endpoint != null;
    }

    @Override
    public boolean bind(NetEndpoint<UID> endpoint) {
        if (this.endpoint != null)
            return false;
        synchronized (this) {
            if (this.endpoint != null)
                return false;
            if (this.getCertificate().isSameCertificate(endpoint.getCertificate())) {
                this.endpoint = endpoint;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean open() {
        if (this.isAvailable())
            return true;
        if (this.isClosed())
            return false;
        lock.lock();
        try {
            if (this.isAvailable())
                return true;
            if (this.isClosed())
                return false;
            if (!this.onOpen())
                return false;
        } finally {
            lock.unlock();
        }
        this.state = TunnelState.ACTIVATE;
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
        if (this == o) return true;
        if (!(o instanceof AbstractNetTunnel)) return false;
        AbstractNetTunnel<?> that = (AbstractNetTunnel<?>) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
