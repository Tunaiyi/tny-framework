package com.tny.game.net.transport;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.context.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.message.*;
import org.slf4j.*;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * 抽象通道
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class AbstractNetTunnel<UID> extends AbstractCommunicator<UID> implements NetTunnel<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractNetTunnel.class);

    private final long id;

    /* 附加属性 */
    private Attributes attributes;

    /**
     * 认证
     */
    private Certificate<UID> certificate;

    private MessageEventsBox<UID> eventsBox;

    /* 接收排除消息模型 */
    private Set<MessageMode> receiveExcludes = ImmutableSet.of();

    /* 发送排除消息模型 */
    private Set<MessageMode> sendExcludes = ImmutableSet.of();

    private NetSession<UID> session;

    private MessageBuilderFactory<UID> messageBuilderFactory;

    private MessageInputEventHandler<UID, NetTunnel<UID>> inputEventHandler;

    private MessageOutputEventHandler<UID, NetTunnel<UID>> outputEventHandler;

    protected AbstractNetTunnel(Certificate<UID> certificate) {
        super(MessageIdCreator.TUNNEL_MESSAGE_ID_MARK);
        this.eventsBox = MessageEventsBox.create();
        this.certificate = certificate;
        this.id = NetAide.newTunnelID();
    }

    protected AbstractNetTunnel<UID> setMessageBuilderFactory(MessageBuilderFactory<UID> messageBuilderFactory) {
        this.messageBuilderFactory = messageBuilderFactory;
        return this;
    }

    protected AbstractNetTunnel<UID> setInputEventHandler(MessageInputEventHandler<UID, NetTunnel<UID>> inputEventHandler) {
        this.inputEventHandler = inputEventHandler;
        return this;
    }

    protected AbstractNetTunnel<UID> setOutputEventHandler(MessageOutputEventHandler<UID, NetTunnel<UID>> outputEventHandler) {
        this.outputEventHandler = outputEventHandler;
        return this;
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

    @Override
    public MessageEventsBox<UID> getEventsBox() {
        return eventsBox;
    }

    @Override
    public MessageBuilderFactory<UID> getMessageBuilderFactory() {
        return messageBuilderFactory;
    }

    private void addInputEvent(MessageInputEvent<UID> event) {
        this.eventsBox.addInputEvent(event);
    }

    private void addOutputEvent(MessageOutputEvent<UID> event) {
        this.eventsBox.addOutputEvent(event);
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
            throw new ValidatorFailException(format("{} 已经授权", this));
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
    public void receive(Message<UID> message) {
        NetLogger.logReceive(this, message);
        MessageMode mode = message.getMode();
        if (this.isExcludeReceiveMode(mode)) {
            LOGGER.debug("{}无法接收 {} 信息[{}]", this, mode, message.getProtocol());
            if (mode == MessageMode.REQUEST)
                this.send(MessageContext.toResponse(message, NetResultCode.NO_RECEIVE_MODE));
            return;
        }
        switch (mode) {
            case PUSH:
            case REQUEST:
            case RESPONSE:
                RespondFuture<UID> future = null;
                if (MessageMode.RESPONSE.isMode(message)) {
                    MessageHeader header = message.getHeader();
                    long toMessage = header.getToMessage();
                    future = this.removeFuture(toMessage);
                }
                this.addInputEvent(receiveEvent(this, message, future));
                this.inputEventHandler.onInput(this);
                return;
            case PING:
                this.pong();
                return;
            default:
        }
    }

    @Override
    public void send(MessageContext<UID> context) {
        try {
            MessageMode mode = context.getMode();
            if (this.isExcludeSendMode(mode)) {
                LOGGER.warn("{} can not send message mode {}", this, context.getMode());
                context.sendFailed(new TunnelException("can not send message mode {}", context.getMode()));
            } else {
                switch (context.getMode()) {
                    case PUSH:
                    case REQUEST:
                    case RESPONSE:
                        this.addOutputEvent(sendEvent(this, context));
                        this.outputEventHandler.onOutput(this);
                        break;
                    case PING:
                        this.ping();
                        break;
                    case PONG:
                        this.pong();
                        break;
                    default:
                }
            }
        } catch (Throwable e) {
            LOGGER.warn("send exception: {} | message: {}", e.getClass(), e.getMessage());
            if (context.isHasFuture())
                context.sendFailed(e);
            throw new NetException(format("{} send {} failed", this, context), e);
        }
    }

    @Override
    public void resend(ResendMessage<UID> message) throws NetException {
        try {
            this.addOutputEvent(resendEvent(this, message));
            this.outputEventHandler.onOutput(this);
        } catch (Throwable e) {
            LOGGER.warn("resend exception: {} | message: {}", e.getClass(), e.getMessage());
            throw new NetException(format("{} resend {} failed", this, message), e);
        }
    }

    @Override
    public void write(Message<UID> message, WriteCallback<UID> callback) throws TunnelWriteException {
        this.doWrite(message, callback);
    }

    private MessageReceiveEvent<UID> receiveEvent(NetTunnel<UID> tunnel, Message<UID> message, RespondFuture<UID> future) {
        return new MessageReceiveEvent<>(tunnel, message, future);
    }

    private MessageSendEvent<UID> sendEvent(NetTunnel<UID> tunnel, MessageContext<UID> content) {
        return new MessageSendEvent<>(tunnel, content);
    }

    private MessageResendEvent<UID> resendEvent(NetTunnel<UID> tunnel, ResendMessage<UID> message) {
        return new MessageResendEvent<>(tunnel, message);
    }


    @Override
    public void ping() {
        if (!isClosed()) {
            try {
                this.doWrite(DetectMessage.ping(), null);
            } catch (TunnelWriteException e) {
                LOGGER.warn("{} | ping", e);
            }
        }
    }

    @Override
    public void pong() {
        if (!isClosed()) {
            try {
                this.doWrite(DetectMessage.pong(), null);
            } catch (TunnelWriteException e) {
                LOGGER.warn("{} | pong", e);
            }
        }
    }

    protected abstract void doWrite(Message<UID> message, WriteCallback<UID> callback) throws TunnelWriteException;

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

    // @Override
    // public Session<UID> getSession() {
    //     return session;
    // }

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
    public Optional<NetSession<UID>> getSession() {
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

    /**
     * 注册回调future对象
     *
     * @param messageId 消息 Id
     */
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

}
