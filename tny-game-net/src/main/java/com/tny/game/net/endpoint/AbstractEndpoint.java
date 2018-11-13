package com.tny.game.net.endpoint;

import com.tny.game.common.context.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-19 14:27
 */
public abstract class AbstractEndpoint<UID> extends AbstractNetter<UID> implements NetEndpoint<UID>, Endpoint<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractEndpoint.class);

    private volatile long id;

    /* 身份凭证 */
    protected Certificate<UID> certificate;

    /* 附加属性 */
    private Attributes attributes;

    /* 接收消息过滤器 */
    private volatile MessageHandleFilter<UID> receiveFilter = NoneMessageHandleFilter.getInstance();

    /* 发送消息过滤器 */
    private volatile MessageHandleFilter<UID> sendFilter = NoneMessageHandleFilter.getInstance();

    protected AbstractEndpoint(Certificate<UID> certificate, int mark) {
        super(mark);
        this.certificate = certificate;
        this.id = NetAide.newEndpointId();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public UID getUserId() {
        return certificate.getUserId();
    }

    @Override
    public String getUserType() {
        return certificate.getUserType();
    }

    @Override
    public boolean isLogin() {
        return this.certificate.isAutherized();
    }

    @Override
    public Certificate<UID> getCertificate() {
        return certificate;
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
    public void setSendFilter(MessageHandleFilter<UID> filter) {
        if (filter == null)
            filter = NoneMessageHandleFilter.getInstance();
        this.sendFilter = filter;
    }

    @Override
    public void setReceiveFilter(MessageHandleFilter<UID> filter) {
        if (filter == null)
            filter = NoneMessageHandleFilter.getInstance();
        this.receiveFilter = filter;
    }

    @Override
    public boolean receive(Tunnel<UID> tunnel, Message<UID> message) {
        MessageMode mode = message.getMode();
        if (mode == MessageMode.RESPONSE)
            this.callbackFuture(message);
        MessageHandleFilter<UID> filter = this.getReceiveFilter();
        if (filter != null && !filter.isCanHandler(message)) {
            LOGGER.warn(format("{} cannot receive {} from {} after being filtered by {}",
                    this, message, tunnel, filter.getClass()));
            return false;
        }
        return true;
    }

    @Override
    public SendContext<UID> sendAsyn(Tunnel<UID> tunnel, MessageContext<UID> messageContext) {
        try {
            if (tunnel == null)
                tunnel = select(messageContext);
            Message<UID> message = tunnel.createMessage(createMessageID(), messageContext);
            return send(tunnel, message, messageContext, 0);
        } catch (Throwable e) {
            completeExceptionally(messageContext, e);
        }
        return ifNull(messageContext, EmptySendContext.empty());
    }

    @Override
    public SendContext<UID> sendSync(Tunnel<UID> tunnel, MessageContext<UID> messageContext, long timeout) throws NetException {
        try {
            if (tunnel == null)
                tunnel = select(messageContext);
            Message<UID> message = tunnel.createMessage(createMessageID(), messageContext);
            return send(tunnel, message, messageContext, timeout);
        } catch (RuntimeException e) {
            completeExceptionally(messageContext, e);
            throw e;
        }
    }

    @Override
    public SendContext<UID> sendAsyn(MessageContext<UID> messageContext) {
        return this.sendAsyn(null, messageContext);
    }

    @Override
    public SendContext<UID> sendSync(MessageContext<UID> messageContext, long timeout) throws NetException {
        return this.sendSync(null, messageContext, timeout);
    }

    private SendContext<UID> send(Tunnel<UID> tunnel, Message<UID> message, MessageContext<UID> context, long waitForSendTimeout) throws NetException {
        try {
            MessageHandleFilter<UID> filter = this.getSendFilter();
            if (filter != null && !filter.isCanHandler(message)) {
                this.completeCancel(context);
                LOGGER.warn(format("{} cannot send {} from {} after being filtered by {}",
                        this, message, tunnel, filter.getClass()));
                return ifNull(context, EmptySendContext.empty());
            }
            return tunnel.write(message, context, waitForSendTimeout, this);
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

    private NetTunnel<UID> select(MessageContext<UID> messageContext) throws SessionException {
        if (isClosed())
            throw new SessionException("Session [{}] 已经关闭, 无法发送", this);
        NetTunnel<UID> tunnel = selectTunnel(messageContext);
        if (tunnel == null)
            throw new NullPointerException(format("{} is no tunnel", this));
        return tunnel;
    }

    protected abstract NetTunnel<UID> selectTunnel(MessageContext<UID> messageContext);

    @Override
    public MessageHandleFilter<UID> getSendFilter() {
        return as(this.sendFilter);
    }

    @Override
    public MessageHandleFilter<UID> getReceiveFilter() {
        return as(this.receiveFilter);
    }

}
