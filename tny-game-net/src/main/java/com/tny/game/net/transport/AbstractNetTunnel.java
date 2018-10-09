package com.tny.game.net.transport;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.context.*;
import com.tny.game.common.event.BindVoidEventBus;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.listener.*;
import com.tny.game.net.transport.message.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * 抽象通道
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class AbstractNetTunnel<UID> extends AbstractCommunicator<UID> implements NetTunnel<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractNetTunnel.class);

    private final long id;

    private TunnelMode mode;

    /* 附加属性 */
    private Attributes attributes;

    /* 认证 */
    private Certificate<UID> certificate;

    /* 接收排除消息模型 */
    private Set<MessageMode> receiveExcludes = ImmutableSet.of();

    /* 发送排除消息模型 */
    private Set<MessageMode> sendExcludes = ImmutableSet.of();

    private MessageHandler<UID> messageHandler;

    protected NetSession<UID> session;

    protected MessageFactory<UID> messageBuilderFactory;

    protected AbstractNetTunnel(Certificate<UID> certificate, TunnelMode mode) {
        super(MessageIdCreator.TUNNEL_MESSAGE_ID_MARK);
        this.mode = mode;
        this.certificate = certificate;
        this.id = NetAide.newTunnelID();
    }

    protected AbstractNetTunnel<UID> setMessageFactory(MessageFactory<UID> messageFactory) {
        this.messageBuilderFactory = messageFactory;
        return this;
    }

    protected AbstractNetTunnel<UID> setMessageHandler(MessageHandler<UID> messageHandler) {
        this.messageHandler = messageHandler;
        return this;
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
    public Certificate<UID> getCertificate() {
        return certificate;
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
    }

    @Override
    protected long createMessageID() {
        NetSession<UID> session = this.session;
        if (session != null)
            return session.createMessageId();
        return super.createMessageID();
    }


    @Override
    public void receive(NetMessage<UID> message) {
        NetLogger.logReceive(this, message);
        MessageMode mode = message.getMode();
        if (this.isExcludeReceiveMode(mode)) {
            LOGGER.warn("{} can not receive message {} mode is {}", this, message, mode);
            if (mode == MessageMode.REQUEST)
                this.sendAsyn(MessageSubjectBuilder
                        .respondBuilder(message.getHeader(), NetResultCode.NO_RECEIVE_MODE, message.getId())
                        .build());
            if (mode == MessageMode.REQUEST)
                this.callbackFuture(message);
            return;
        }
        switch (mode) {
            case PUSH:
            case REQUEST:
            case RESPONSE:
                this.messageHandler.handle(this, message);
                return;
            case PING:
                this.pong();
                return;
            default:
        }
    }

    @Override
    public void resend(long from, long to) throws NetException {
        if (this.session == null)
            throw new NetException(format("{} no bind session", this));
        List<Message<UID>> messages = this.session.getSentMessages(from, to);
        if (messages.isEmpty())
            throw new NetException(format("form {} to {} cached message is missing", from, to, this));
        for (Message<UID> message : messages)
            doWrite(message);
    }

    @Override
    public SendContext<UID> sendAsyn(MessageSubject subject, MessageContext<UID> messageContext) {
        if (this.checkExcludeSendMode(subject, messageContext))
            return ifNull(messageContext, EmptySendContext.empty());
        write(subject, messageContext);
        return ifNull(messageContext, EmptySendContext.empty());
    }

    @Override
    public SendContext<UID> sendSync(MessageSubject subject, MessageContext<UID> messageContext, long timeout) {
        if (this.checkExcludeSendMode(subject, messageContext))
            return ifNull(messageContext, EmptySendContext.empty());
        if (messageContext == null)
            messageContext = MessageContexts.createContext();
        write(subject, messageContext, timeout);
        if (!messageContext.getSendFuture().awaitUninterruptibly(timeout + 3000, TimeUnit.MILLISECONDS))
            throw new SendTimeoutException(format("{} send message {} timeout", this, subject));
        return ifNull(messageContext, EmptySendContext.empty());
    }

    private boolean checkExcludeSendMode(MessageSubject subject, MessageContext<UID> messageContext) {
        MessageMode mode = subject.getMode();
        if (this.isExcludeSendMode(mode)) {
            LOGGER.warn("{} can not send message {} mode is {}", this, subject, mode);
            TunnelException cause = new TunnelException("{} can not send message {} mode is {}", this, subject, mode);
            completeExceptionally(messageContext, cause);
            return true;
        }
        return false;
    }

    protected void write(MessageSubject subject, MessageContext<UID> context) {
        try {
            write(subject, context, 0);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    private void write(MessageSubject subject, MessageContext<UID> context, long waitForSendTimeout) {
        doWrite(subject, context, waitForSendTimeout);
    }

    protected abstract void doWrite(Message<UID> message);

    protected abstract void doWrite(MessageSubject message, MessageContext<UID> context, long waitForSendTimeout) throws NetException;

    @Override
    public void ping() {
        if (!isActive()) {
            this.doWrite(DetectMessage.ping());
        }
    }

    @Override
    public void pong() {
        if (!isActive()) {
            this.doWrite(DetectMessage.pong());
        }
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
    public void excludeReceiveModes(Collection<MessageMode> modes) {
        this.receiveExcludes = ImmutableSet.copyOf(modes);
    }

    @Override
    public void excludeSendModes(Collection<MessageMode> modes) {
        this.sendExcludes = ImmutableSet.copyOf(modes);
    }

    @Override
    public boolean isExcludeReceiveMode(MessageMode mode) {
        return this.receiveExcludes.contains(mode);
    }

    @Override
    public boolean isExcludeSendMode(MessageMode mode) {
        return this.sendExcludes.contains(mode);
    }

    @Override
    public Optional<NetSession<UID>> getBindSession() {
        return Optional.ofNullable(session);
    }

    @Override
    public boolean bind(NetSession<UID> session) {
        if (this.session != null)
            return false;
        if (this.getCertificate().isSameCertificate(session.getCertificate())) {
            this.session = session;
            return true;
        }
        return false;
    }

    @Override
    public boolean isBind() {
        return this.session != null;
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

    @Override
    public void registerFuture(long messageId, RespondFuture<UID> respondFuture) {
        if (respondFuture == null)
            return;
        if (MessageIdCreator.getMark(messageId) == MessageIdCreator.SESSION_MESSAGE_ID_MARK) {
            NetSession<UID> session = this.session;
            if (session != null)
                session.registerFuture(messageId, respondFuture);
            else
                respondFuture.cancel(true);
        } else {
            super.registerFuture(messageId, respondFuture);
        }
    }

    @Override
    public void callbackFuture(Message<UID> message) {
        if (message.getMode() != MessageMode.RESPONSE)
            return;
        RespondFuture<UID> future = this.removeFuture(message.getHeader().getToMessage());
        if (future != null)
            future.complete(message);
    }

    @Override
    protected RespondFuture<UID> removeFuture(long messageId) {
        if (MessageIdCreator.getMark(messageId) == MessageIdCreator.SESSION_MESSAGE_ID_MARK) {
            NetSession<UID> session = this.session;
            if (session != null)
                return session.removeFuture(messageId);
            return null;
        } else {
            return super.removeFuture(messageId);
        }
    }

    protected BindVoidEventBus<TunnelOpenListener, Tunnel> openEvent() {
        return TunnelEvents.ON_OPEN;
    }

    protected BindVoidEventBus<TunnelCloseListener, Tunnel> closeEvent() {
        return TunnelEvents.ON_CLOSE;
    }

    protected MessageHandler<UID> getMessageHandler() {
        return messageHandler;
    }

}
