package com.tny.game.net.transport;

import com.tny.game.common.context.*;
import com.tny.game.common.event.BindVoidEventBus;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.listener.*;
import org.slf4j.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.ifNull;
import static com.tny.game.common.utils.StringAide.*;

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

    protected AbstractNetTunnel(Certificate<UID> certificate, TunnelMode mode) {
        super(MessageIdCreator.TUNNEL_MESSAGE_ID_MARK);
        this.mode = mode;
        this.certificate = certificate;
        this.id = NetAide.newTunnelId();
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
        return state == TunnelState.ALIVE;
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
        this.authenticateEvent().notify(this);
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
                    if (endpoint != null)
                        return endpoint.receive(this, message);
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
    public SendContext<UID> sendAsyn(MessageSubject subject, MessageContext<UID> context) {
        try {
            NetEndpoint<UID> endpoint = this.endpoint;
            if (endpoint != null)
                return endpoint.sendAsyn(this, subject, context);
            else
                return send(subject, context, 0);
        } catch (Throwable e) {
            return ifNull(context, EmptySendContext.empty());
        }
    }

    @Override
    public SendContext<UID> sendSync(MessageSubject subject, MessageContext<UID> context, long timeout) {
        NetEndpoint<UID> endpoint = this.endpoint;
        if (endpoint != null)
            return endpoint.sendSync(this, subject, context, timeout);
        else
            return send(subject, context, timeout);
    }

    protected SendContext<UID> send(MessageSubject subject, MessageContext<UID> context, long waitForSendTimeout) throws NetException {
        try {
            Message<UID> message = this.createMessage(subject, context);
            return write(message, context, waitForSendTimeout, this);
        } catch (RuntimeException e) {
            LOGGER.error("", e);
            this.completeExceptionally(context, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("", e);
            this.completeExceptionally(context, e);
            throw new NetException(e);
        }
    }

    @Override
    public SendContext<UID> write(Message<UID> message, MessageContext<UID> context, long waitForSendTimeout, WriteCallback<UID> callback) throws NetException {
        try {
            return doWrite(message, context, waitForSendTimeout, callback);
        } catch (Throwable e) {
            LOGGER.error("", e);
            this.completeExceptionally(context, e);
            throw e;
        }
    }

    protected abstract SendContext<UID> doWrite(Message<UID> message, MessageContext<UID> context, long waitForSendTimeout, WriteCallback<UID> callback) throws NetException;

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
        synchronized (this) {
            if (this.isAvailable())
                return true;
            if (this.isClosed())
                return false;
            if (!this.onOpen())
                return false;
        }
        this.state = TunnelState.ALIVE;
        this.openEvent().notify(this);
        return true;
    }

    @Override
    public void disconnect() {
        synchronized (this) {
            if (state == TunnelState.CLOSE || state == TunnelState.UNALIVE)
                return;
            this.doDisconnect();
            this.state = TunnelState.UNALIVE;
        }
        this.unaliveEvent().notify(this);
        NetEndpoint<UID> endpoint = this.endpoint;
        if (endpoint != null)
            endpoint.onDisable(this);
    }

    @Override
    public void close() {
        if (state == TunnelState.CLOSE)
            return;
        synchronized (this) {
            if (state == TunnelState.CLOSE)
                return;
            this.onClose();
            this.state = TunnelState.CLOSE;
        }
        this.closeEvent().notify(this);
        NetEndpoint<UID> endpoint = this.endpoint;
        if (endpoint != null)
            endpoint.onDisable(this);
    }

    protected abstract void onClose();

    protected abstract boolean onOpen();

    protected abstract void doDisconnect();

    protected BindVoidEventBus<TunnelAuthenticateListener, Tunnel> authenticateEvent() {
        return TunnelEvents.ON_AUTHENTICATE;
    }

    protected BindVoidEventBus<TunnelOpenListener, Tunnel> openEvent() {
        return TunnelEvents.ON_OPEN;
    }

    protected BindVoidEventBus<TunnelUnaliveListener, Tunnel> unaliveEvent() {
        return TunnelEvents.ON_UNALIVE;
    }

    protected BindVoidEventBus<TunnelCloseListener, Tunnel> closeEvent() {
        return TunnelEvents.ON_CLOSE;
    }

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
