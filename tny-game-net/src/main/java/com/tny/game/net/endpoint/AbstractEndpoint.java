package com.tny.game.net.endpoint;

import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.event.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.endpoint.listener.EndpointEventBuses.*;

/**
 * <p>
 */
public abstract class AbstractEndpoint<UID> extends AbstractNetter<UID> implements NetEndpoint<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractEndpoint.class);

    /* 终端 ID */
    private volatile long id;

    /* 通讯管道 */
    protected volatile NetTunnel<UID> tunnel;

    /* 认证 */
    protected Certificate<UID> certificate;

    /* 状态 */
    private volatile EndpointStatus state;

    /* ID 生成器 */
    private final AtomicLong idCreator = new AtomicLong(0);

    /* 消息盒子 */
    private final EndpointEventsBox<UID> eventsBox;

    /* 输入事件 */
    private final EndpointEventsBoxHandler<UID, NetEndpoint<UID>> eventHandler;

    /* 写出的消息缓存 */
    private final MessageQueue<UID> sentMessageQueue;

    /* 响应 future 管理器 */
    private volatile RespondFutureHolder respondFutureHolder;

    /* 离线时间 */
    protected volatile long offlineTime;

    /* 接收消息过滤器 */
    private volatile MessageHandleFilter<UID> receiveFilter = MessageHandleFilter.allHandleFilter();

    /* 发送消息过滤器 */
    private volatile MessageHandleFilter<UID> sendFilter = MessageHandleFilter.allHandleFilter();

    protected AbstractEndpoint(Certificate<UID> certificate, EndpointEventsBoxHandler<UID, ? extends NetEndpoint<UID>> eventHandler,
            int cacheSentMessageSize) {
        this.certificate = certificate;
        this.state = EndpointStatus.INIT;
        this.eventsBox = new EndpointEventsBox<>();
        this.eventHandler = as(eventHandler);
        this.id = NetAide.newEndpointId();
        this.sentMessageQueue = new MessageQueue<>(cacheSentMessageSize);
    }

    private RespondFutureHolder futureHolder() {
        if (this.respondFutureHolder != null) {
            return this.respondFutureHolder;
        }
        synchronized (this) {
            if (this.respondFutureHolder != null) {
                return this.respondFutureHolder;
            }
            return this.respondFutureHolder = RespondFutureHolder.getHolder(this);
        }
    }

    private void putFuture(long messageId, RespondFuture<UID> respondFuture) {
        if (respondFuture == null) {
            return;
        }
        futureHolder().putFuture(messageId, respondFuture);
    }

    private <M> RespondFuture<M> pollFuture(Message message) {
        RespondFutureHolder respondFutureHolder = this.respondFutureHolder;
        if (respondFutureHolder == null) {
            return null;
        }
        if (message.getMode() == MessageMode.RESPONSE) {
            // todo IO 线程处理
            return respondFutureHolder.pollFuture(message.getToMessage());
        }
        return null;
    }

    @Override
    public RespondFutureHolder getRespondFutureHolder() {
        return this.respondFutureHolder;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setSendFilter(MessageHandleFilter<UID> filter) {
        if (filter == null) {
            filter = MessageHandleFilter.allHandleFilter();
        }
        this.sendFilter = filter;
    }

    @Override
    public void setReceiveFilter(MessageHandleFilter<UID> filter) {
        if (filter == null) {
            filter = MessageHandleFilter.allHandleFilter();
        }
        this.receiveFilter = filter;
    }

    @Override
    public boolean receive(NetTunnel<UID> tunnel, Message message) {
        RespondFuture<UID> future = pollFuture(message);
        if (this.isClosed()) {
            return false;
        }
        MessageHandleFilter<UID> filter = this.getReceiveFilter();
        if (filter != null) {
            boolean throwable = true;
            switch (filter.filter(this, message)) {
                case IGNORE:
                    throwable = false;
                case THROW:
                    if (throwable) {
                        String causeMessage = format("{} cannot receive {} from {} after being filtered by {}",
                                this, message, tunnel, filter.getClass());
                        LOGGER.warn(causeMessage);
                        throw new EndpointException(causeMessage);
                    }
                    return false;
                default:
                    break;
            }
        }
        this.eventsBox.addInputEvent(new EndpointReceiveEvent<>(tunnel, message, future));
        this.eventHandler.onInput(this.eventsBox, this);
        //        this.eventHandler.onInputEvent(this, new EndpointReceiveEvent<>(tunnel, message, future));
        return true;
    }

    @Override
    public SendContext<UID> send(NetTunnel<UID> tunnel, MessageContext<UID> context) {
        try {
            tryCreateFuture(context);
            if (this.isClosed()) {
                context.fail(new EndpointCloseException(format("endpoint {} closed", this)));
                return context;
            }
            if (tunnel == null) {
                tunnel = currentTunnel();
            }
            MessageHandleFilter<UID> filter = this.getSendFilter();
            if (filter != null) {
                boolean throwable = true;
                switch (filter.filter(this, context)) {
                    case IGNORE:
                        throwable = false;
                    case THROW:
                        context.cancel(true);
                        String causeMessage = format("{} cannot send {} to {} after being filtered by {}", this, context, tunnel, filter.getClass());
                        LOGGER.warn(causeMessage);
                        if (throwable) {
                            throw new EndpointException(causeMessage);
                        }
                        return context;
                    case HANDLE:
                        break;
                }
            }
            //            this.eventsBox.addOutputEvent(new EndpointSendEvent<>(tunnel, context));
            //            this.eventHandler.onOutput(this.eventsBox, this);
            tunnel.write(this, context);
            //            this.eventHandler.onOutputEvent(this, new EndpointSendEvent<>(tunnel, context));
            return context;
        } catch (Exception e) {
            LOGGER.error("", e);
            context.fail(e);
            throw new NetException(e);
        }
    }

    @Override
    public boolean receive(Message message) {
        return receive(null, message);
    }

    @Override
    public void resend(NetTunnel<UID> tunnel, Predicate<Message> filter) {
        if (this.isClosed()) {
            return;
        }
        if (tunnel == null) {
            tunnel = currentTunnel();
        }
        //        this.eventsBox.addOutputEvent(new EndpointResendEvent<>(tunnel, filter));
        //        this.eventHandler.onOutput(this.eventsBox, this);
        tunnel.writeBatch(() -> this.getSentMessages(filter));
    }

    @Override
    public void resend(NetTunnel<UID> tunnel, long fromId, FilterBound bound) {
        if (this.isClosed()) {
            return;
        }
        if (tunnel == null) {
            tunnel = currentTunnel();
        }
        tunnel.writeBatch(() -> this.getSentMessages(fromId, bound));
    }

    @Override
    public void resend(NetTunnel<UID> tunnel, long fromId, long toId, FilterBound bound) {
        if (this.isClosed()) {
            return;
        }
        if (tunnel == null) {
            tunnel = currentTunnel();
        }
        tunnel.writeBatch(() -> this.getSentMessages(fromId, toId, bound));
    }

    @Override
    public void takeOver(EndpointEventsBox<UID> eventsBox) {
        this.eventsBox.accept(eventsBox);
    }

    @Override
    public SendContext<UID> send(MessageContext<UID> messageContext) {
        return this.send(null, messageContext);
    }

    protected NetTunnel<UID> currentTunnel() {
        return this.tunnel;
    }

    @Override
    public MessageHandleFilter<UID> getSendFilter() {
        return as(this.sendFilter);
    }

    @Override
    public MessageHandleFilter<UID> getReceiveFilter() {
        return as(this.receiveFilter);
    }

    @Override
    public EndpointEventsBox<UID> getEventsBox() {
        return this.eventsBox;
    }

    @Override
    public void writeMessage(NetTunnel<UID> tunnel, MessageContext<UID> context) {
        MessageFactory<UID> messageFactory = this.tunnel.getMessageFactory();
        Message message = messageFactory.create(createMessageId(), context);
        this.tryCreateFuture(context);
        RespondFuture<UID> respondFuture = context.getRespondFuture();
        if (respondFuture != null) {
            this.putFuture(message.getId(), respondFuture);
        }
        this.sentMessageQueue.addMessage(message);
        this.tunnel.write(message, as(context.getWriteMessageFuture()));
    }

    @Override
    public Message createMessage(MessageContext<UID> context) {
        MessageFactory<UID> messageFactory = this.tunnel.getMessageFactory();
        Message message = messageFactory.create(createMessageId(), context);
        this.tryCreateFuture(context);
        RespondFuture<UID> respondFuture = context.getRespondFuture();
        if (respondFuture != null) {
            this.putFuture(message.getId(), respondFuture);
        }
        this.sentMessageQueue.addMessage(message);
        return message;
        //        this.tunnel.write(message, context.getWriteMessagePromise());
    }

    private void tryCreateFuture(MessageContext<UID> context) {
        if (context.isNeedResponseFuture() || context.isNeedWriteFuture()) {
            WriteMessagePromise promise = as(context.getWriteMessageFuture());
            if (promise == null) {
                promise = this.tunnel.createWritePromise(context.getWriteTimeout());
                context.setWriteMessagePromise(promise);
            }
            if (context.isNeedResponseFuture()) {
                RespondFuture<UID> respondFuture = context.getRespondFuture();
                if (respondFuture == null) {
                    respondFuture = new RespondFuture<>();
                }
                context.setRespondFuture(respondFuture);
                promise.setRespondFuture(respondFuture);
            }
        }
    }

    private long createMessageId() {
        return this.idCreator.incrementAndGet();
    }

    @Override
    public List<Message> getSentMessages(Predicate<Message> filter) {
        return this.sentMessageQueue.getMessages(filter);
    }

    @Override
    public List<Message> getAllSendMessages() {
        return this.sentMessageQueue.getAllMessages();
    }

    @Override
    public EndpointStatus getStatus() {
        return this.state;
    }

    @Override
    public EndpointEventsBoxHandler<UID, NetEndpoint<UID>> getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public long getOfflineTime() {
        return this.offlineTime;
    }

    protected void setOnline() {
        this.offlineTime = 0;
        this.state = EndpointStatus.ONLINE;
        buses().onlineEvent().notify(this);
    }

    protected void setOffline() {
        this.offlineTime = System.currentTimeMillis();
        this.state = EndpointStatus.OFFLINE;
        buses().offlineEvent().notify(this);
    }

    protected void setClose() {
        this.state = EndpointStatus.CLOSE;
        this.destroyFutureHolder();
        buses().closeEvent().notify(this);
    }

    @Override
    public boolean isOnline() {
        return this.state == EndpointStatus.ONLINE;
    }

    @Override
    public boolean isOffline() {
        return this.state == EndpointStatus.OFFLINE;
    }

    @Override
    public boolean isClosed() {
        return this.state == EndpointStatus.CLOSE;
    }

    private void offlineIf(NetTunnel<UID> tunnel) {
        synchronized (this) {
            if (tunnel != currentTunnel()) {
                return;
            }
            if (!tunnel.isClosed()) {
                tunnel.close();
            }
            setOffline();
        }
    }

    @Override
    public void offline() {
        synchronized (this) {
            NetTunnel<UID> tunnel = currentTunnel();
            if (!tunnel.isClosed()) {
                tunnel.close();
            }
            setOffline();
        }
    }

    @Override
    public void heartbeat() {
        NetTunnel<UID> tunnel = currentTunnel();
        if (tunnel.isActive()) {
            tunnel.ping();
        }
    }

    @Override
    public void close() {
        if (this.state == EndpointStatus.CLOSE) {
            return;
        }
        synchronized (this) {
            if (this.state == EndpointStatus.CLOSE) {
                return;
            }
            this.offline();
            this.setClose();
        }
    }

    private void checkOnlineCertificate(Certificate<UID> certificate) {
        Certificate<UID> currentCert = this.certificate;
        if (!certificate.isAuthenticated()) {
            throw new ValidatorFailException(NetResultCode.UNLOGIN);
        }
        if (currentCert != null && currentCert.isAuthenticated() && !currentCert.isSameCertificate(certificate)) { // 是否是同一个授权
            throw new ValidatorFailException(format("Certificate new [{}] 与 old [{}] 不同", certificate, this.certificate));
        }
        if (this.isClosed()) // 判断 session 状态是否可以重登
        {
            throw new ValidatorFailException(NetResultCode.SESSION_LOSS);
        }
    }

    @Override
    public void online(Certificate<UID> certificate, NetTunnel<UID> tunnel) throws ValidatorFailException {
        ThrowAide.checkNotNull(tunnel, "newSession is null");
        checkOnlineCertificate(certificate);
        synchronized (this) {
            checkOnlineCertificate(certificate);
            this.certificate = certificate;
            this.acceptTunnel(tunnel);
        }
    }

    // 接受 Tunnel
    private void acceptTunnel(NetTunnel<UID> newTunnel) throws ValidatorFailException {
        if (newTunnel.bind(this)) {
            NetTunnel<UID> oldTunnel = this.tunnel;
            this.tunnel = newTunnel;
            this.offlineTime = 0;
            if (oldTunnel != null && newTunnel != oldTunnel) {
                oldTunnel.close();  // 关闭旧 Tunnel
            }
            this.setOnline();
        } else {
            this.offlineIf(newTunnel);
            throw new ValidatorFailException(format("{} tunnel is bound session", newTunnel));
        }
    }

}
