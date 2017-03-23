package com.tny.game.net.common.session;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.tny.game.LogUtils;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.exception.SessionException;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageBuilder;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.message.Protocol;
import com.tny.game.net.session.MessageFuture;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.SessionInputEventHandler;
import com.tny.game.net.session.SessionOutputEventHandler;
import com.tny.game.net.session.SessionPushOption;
import com.tny.game.net.session.SessionState;
import com.tny.game.net.session.event.SessionEvent.SessionEventType;
import com.tny.game.net.session.event.SessionInputEvent;
import com.tny.game.net.session.event.SessionOutputEvent;
import com.tny.game.net.session.event.SessionReceiveEvent;
import com.tny.game.net.session.event.SessionResendEvent;
import com.tny.game.net.session.event.SessionSendEvent;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

/**
 * 通用Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public abstract class CommonSession<UID, S extends CommonSession<UID, S>> implements NetSession<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(CommonSession.class);

    private int id;

    /* 认证 */
    protected LoginCertificate<UID> certificate;

    /* 检测器 */
    protected List<ControllerChecker> checkers = new CopyOnWriteArrayList<>();

    /* 附加属性 */
    private Attributes attributes;

    /* 消息工厂 */
    protected MessageBuilderFactory<UID> messageBuilderFactory;

    /* 消息校验码生成器 */
    protected MessageSignGenerator<UID> messageCheckGenerator;

    /* 接收队列 */
    private ConcurrentLinkedDeque<SessionInputEvent> inputEventQueue = new ConcurrentLinkedDeque<>();

    /* 发送队列 */
    private ConcurrentLinkedDeque<SessionOutputEvent> outputEventQueue = new ConcurrentLinkedDeque<>();

    /* 发送缓存 */
    private CircularFifoQueue<SessionSendEvent> handledSendEventQueue = null;

    private StampedLock handledSendEventQueueLock;

    private int messageIDCounter = 0;

    private volatile long offlineTime;

    private volatile long lastReceiveTime;

    private AtomicInteger sessionState = new AtomicInteger(SessionState.ONLINE.getId());

    private SessionInputEventHandler<UID, NetSession<UID>> inputEventHandler;

    private SessionOutputEventHandler<UID, NetSession<UID>> outputEventHandler;

    @SuppressWarnings("unchecked")
    public CommonSession(SessionOutputEventHandler<UID, S> writer, SessionInputEventHandler<UID, S> reader, int cacheMessageSize) {
        this.outputEventHandler = (SessionOutputEventHandler<UID, NetSession<UID>>) writer;
        this.inputEventHandler = (SessionInputEventHandler<UID, NetSession<UID>>) reader;
        if (cacheMessageSize > 0) {
            this.handledSendEventQueue = new CircularFifoQueue<>(cacheMessageSize);
            handledSendEventQueueLock = new StampedLock();
        }
    }

    @Override
    public long getID() {
        return 0;
    }

    @Override
    public UID getUID() {
        return certificate.getUserID();
    }

    @Override
    public String getGroup() {
        return certificate.getUserGroup();
    }

    @Override
    public DateTime getLoginAt() {
        return certificate.getLoginAt();
    }

    protected MessageSignGenerator<UID> getMessageCheckGenerator() {
        return messageCheckGenerator;
    }

    @Override
    public boolean isLogin() {
        return this.certificate != null && this.certificate.isLogin();
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
    public LoginCertificate<UID> getCertificate() {
        return certificate;
    }

    private boolean checkPushable() {
        SessionPushOption option = this.attributes().getAttribute(SessionPushOption.SESSION_PUSH_OPTION, SessionPushOption.PUSH);
        if (!option.isPush()) {
            if (option.isThrowable())
                throw new SessionException(LogUtils.format("Session {}-{} [{}] 无法推送", this.getCertificate(), this.getGroup(), this.getUID()));
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean transferFrom(NetSession<UID> oldSession) {
        if (!this.isOnline() || this.getCertificate().isRelogin())
            return false;
        if (this.getClass().isInstance(oldSession) && oldSession.invalid()) {
            CommonSession<UID, S> oldOne = (CommonSession<UID, S>) oldSession;
            synchronized (this) {
                this.id = oldOne.id;
                this.messageIDCounter = oldOne.messageIDCounter;
                this.inputEventQueue.addAll(oldOne.inputEventQueue);
                this.outputEventQueue.addAll(oldOne.outputEventQueue);
                //TODO 是否有线程问题
                this.handledSendEventQueue.addAll(oldOne.handledSendEventQueue);
            }
            postTransferTo((S) oldSession);
        }
        return true;
    }

    @Override
    public void sendMessage(Protocol protocol, MessageContent content, boolean sent) {
        if (protocol.isPush() && !checkPushable())
            return;
        MessageBuilder builder = messageBuilderFactory.newMessageBuilder()
                .setBody(content.getBody())
                .setCode(content.getCode())
                .setToMessage(content.getToMessage())
                .setSignGenerator(this.messageCheckGenerator)
                .setTime(System.currentTimeMillis());
        try {
            synchronized (this) {
                Message<?> message = builder.setID(++messageIDCounter)
                        .build();
                NetLogger.logSend(this, message);
                this.outputEventQueue.add(sendEvent(message, content, sent));
                if (content.hasMessageFuture()) {
                    MessageFuture<?> future = content.getMessageFuture()
                            .setSession(this);
                    MessageFuture.putFuture(this, message, future);
                }
                inputEventHandler.onInput(this);
            }
        } catch (Throwable e) {
            LOGGER.error("send response exception", e);
        }
    }

    @Override
    public void receiveMessage(Message<UID> message) {
        NetLogger.logReceive(this, message);
        MessageFuture<?> future = null;
        if (MessageMode.RESPONSE.isMode(message)) {
            int toMessage = message.getToMessage();
            future = MessageFuture.getFuture(this.getID(), toMessage);
        }
        this.inputEventQueue.add(receiveEvent(message, future));
        outputEventHandler.onOutput(this);
        this.lastReceiveTime = System.currentTimeMillis();
    }

    @Override
    public boolean hasInputEvent() {
        return !outputEventQueue.isEmpty();
    }

    @Override
    public boolean hasOutputEvent() {
        return !inputEventQueue.isEmpty();
    }

    @Override
    public void resendMessage(int messageID) {
        resendMessage(Range.singleton(messageID));
    }

    @Override
    public void resendMessages(int fromID) {
        resendMessage(Range.atLeast(fromID));
    }

    @Override
    public void resendMessages(int fromID, int toID) {
        resendMessage(Range.closed(fromID, toID));
    }

    private void resendMessage(Range<Integer> range) {
        this.outputEventQueue.add(new SessionResendEvent(range));
    }

    private SessionSendEvent sendEvent(Message<?> message, MessageContent content, boolean sent) {
        return new SessionSendEvent(message, sent, SessionEventType.MESSAGE, content.getSentHandler());
    }

    private SessionReceiveEvent receiveEvent(Message<?> message, MessageFuture<?> future) {
        return new SessionReceiveEvent(message, SessionEventType.MESSAGE, future);
    }

    @Override
    public List<SessionSendEvent> getHandledSendEvents(Range<Integer> range) {
        if (this.handledSendEventQueue == null)
            return ImmutableList.of();
        StampedLock lock = this.handledSendEventQueueLock;
        long stamp = lock.readLock();
        try {
            return this.handledSendEventQueue.stream()
                    .filter(e -> range.contains(e.getMessage().getID()))
                    .collect(Collectors.toList());
        } finally {
            lock.unlockRead(stamp);
        }
    }

    protected void addHandledOutputEvent(SessionSendEvent event) {
        if (event.getEventType() != SessionEventType.MESSAGE)
            return;
        if (this.handledSendEventQueue != null) {
            StampedLock lock = this.handledSendEventQueueLock;
            long stamp = lock.writeLock();
            try {
                this.handledSendEventQueue.add(event);
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    @Override
    public SessionInputEvent pollInputEvent() {
        if (this.isInvalided())
            return null;
        Queue<SessionInputEvent> inputEventQueue = this.inputEventQueue;
        if (inputEventQueue == null || inputEventQueue.isEmpty())
            return null;
        return inputEventQueue.poll();
    }

    @Override
    public SessionOutputEvent pollOutputEvent() {
        if (this.isInvalided())
            return null;
        Queue<SessionOutputEvent> outputEventQueue = this.outputEventQueue;
        if (outputEventQueue == null || outputEventQueue.isEmpty())
            return null;
        SessionOutputEvent event = outputEventQueue.poll();
        if (event != null && event instanceof SessionSendEvent) {
            addHandledOutputEvent((SessionSendEvent) event);
        }
        return event;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public MessageBuilderFactory<UID> getMessageBuilderFactory() {
        return null;
    }

    @Override
    public void offline(boolean invalid) {
        int state = this.sessionState.get();
        if (invalid) {
            while (state != SessionState.INVALID.getId() && !this.sessionState.compareAndSet(state, SessionState.INVALID.getId())) {
                state = this.sessionState.get();
            }
            this.postInvalid();
        } else {
            if (this.sessionState.compareAndSet(SessionState.ONLINE.getId(), SessionState.OFFLINE.getId())) {
                this.offlineTime = System.currentTimeMillis();
                this.postOffline();
            }
        }
    }

    @Override
    public boolean invalid() {
        if (this.sessionState.compareAndSet(SessionState.OFFLINE.getId(), SessionState.INVALID.getId())) {
            this.postInvalid();
            return true;
        }
        return false;
    }

    @Override
    public boolean isInvalided() {
        return this.sessionState.get() == SessionState.INVALID.getId();
    }

    @Override
    public long getOfflineTime() {
        return offlineTime;
    }

    @Override
    public long getLastReceiveTime() {
        return lastReceiveTime;
    }

    @Override
    public void login(LoginCertificate<UID> certificate) {
        this.certificate = certificate;
    }

    protected abstract void postTransferTo(S newSession);

    protected abstract void postOffline();

    protected abstract void postInvalid();

    // @Override
    // public void pollInputEvent() {
    //     int time = this.sendNumPerTime;
    //     try {
    //         while (time < 0 || time > 0 || this.outputEventQueue.isEmpty()) {
    //             try {
    //                 MessageOrder order = this.outputEventQueue.poll();
    //                 if (order == null)
    //                     break;
    //                 switch (order.getOrderType()) {
    //                     case SEND:
    //                         this.addHandledOutputEvent((SessionSendEvent) order);
    //                         break;
    //                     case RESEND:
    //                         break;
    //                 }
    //             } catch (Throwable e) {
    //                 LOGGER.error("processSendMessages {} ", e);
    //             }
    //         }
    //     } finally {
    //         this.writeMessageLock.set(false);
    //     }
    //     if (this.hasInputEvent())
    //         this.submitToWrite();
    // }
    //
    // @Override
    // public void processReceiveMessage(MessageDispatcher dispatcher) {
    //     int time = this.receiveNamPerTime;
    //     try {
    //         while (time < 0 || time > 0 || this.receiveCommandQueue.isEmpty()) {
    //             try {
    //                 NetMessage message = this.receiveCommandQueue.poll();
    //                 if (message == null)
    //                     break;
    //                 int toMessage = message.getToMessage();
    //                 if (toMessage > 0 && this.futureHolder != null) {
    //                     MessageFuture<?> future = this.futureHolder.takeFuture(message.getToMessage());
    //                     if (future != null)
    //                         future.setResponse(message);
    //                 }
    //                 dispatcher.dispatch(message, this);
    //             } catch (Throwable e) {
    //                 LOGGER.error("processSendMessages {} ", e);
    //             }
    //         }
    //     } finally {
    //         this.readMessageLock.set(false);
    //     }
    //     if (this.hasOutputEvent())
    //         this.submitToRead();
    // }
}
