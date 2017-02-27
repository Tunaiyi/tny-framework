package com.tny.game.net.dispatcher;

import com.google.common.collect.Range;
import com.tny.game.LogUtils;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.MessageCheckGenerator;
import com.tny.game.net.checker.MessageChecker;
import com.tny.game.net.dispatcher.exception.SessionException;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public abstract class CommonNetSession<UID> implements NetSession<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(CommonNetSession.class);

    protected LoginCertificate<UID> certificate;

    protected List<MessageChecker> checkers = new CopyOnWriteArrayList<>();

    private Attributes attributes;

    protected MessageBuilderFactory messageBuilderFactory;

    private ConcurrentLinkedDeque<NetMessage> receiveMessageQueue;

    private ConcurrentLinkedDeque<MessageOrder> sendMessageQueue;

    private CircularFifoQueue<MessageCapsule> sentMessageCache;

    private MessageFutureHolder futureHolder;

    private int messageIDCounter = 0;

    private int processSendNumPerTime = -1;

    private int processReciveNumPerTime = -1;

    private MessageCheckGenerator messageCheckGenerator;

    private MessageWriter writer;

    private MessageReader reader;

    private AtomicBoolean writeLock = new AtomicBoolean(false);

    private AtomicBoolean readLock = new AtomicBoolean(false);

    public CommonNetSession(int cacheMessageSize) {
        if (cacheMessageSize > 0)
            this.sentMessageCache = new CircularFifoQueue<>(cacheMessageSize);
    }

    private void submitToWrite() {
        if (writeLock.get())
            return;
        if (writeLock.compareAndSet(false, true)) {
            writer.write(this);
        }
    }

    private void submitToRead() {
        if (readLock.get())
            return;
        if (readLock.compareAndSet(false, true)) {
            reader.read(this);
        }
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
    public long getLoginAt() {
        return certificate.getLoginAt();
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

    @Override
    public List<MessageChecker> getCheckers() {
        return Collections.unmodifiableList(this.checkers);
    }

    @Override
    public void addChecker(MessageChecker checker) {
        this.checkers.add(checker);
    }

    @Override
    public void removeChecker(MessageChecker checker) {
        this.checkers.remove(checker);
    }

    @Override
    public void removeChecker(Class<? extends MessageChecker> checkClass) {
        for (MessageChecker checker : checkers) {
            if (checkClass.isInstance(checker))
                checkers.remove(checker);
        }
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
    public void sendMessage(Protocol protocol, MessageContent content) {
        if (protocol.isPush() && !checkPushable())
            return;
        if (protocol.isPush() && !checkPushable())
            return;
        MessageBuilder builder = messageBuilderFactory.newMessageBuilder()
                .setBody(content.getBody())
                .setCode(content.getCode())
                .setToMessage(content.getToMessage())
                .setCheckGenerator(this.messageCheckGenerator)
                .setTime(System.currentTimeMillis());
        try {
            synchronized (this) {
                NetMessage message = builder.setID(++messageIDCounter)
                        .build();
                CoreLogger.logSend(this, message);
                this.sendMessageQueue.push(new MessageCapsule(message, content.getSentHandler(), content.getMessageFuture()));
                this.submitToWrite();
            }
        } catch (Throwable e) {
            LOGGER.error("send response exception", e);
        }
    }

    @Override
    public void receiveMessage(NetMessage message) {
        message.setSession(this);
        CoreLogger.logReceive(this, message);
        this.receiveMessageQueue.push(message);
        this.submitToRead();
    }

    @Override
    public boolean hasSendMessage() {
        return !sendMessageQueue.isEmpty();
    }

    @Override
    public boolean hasReceiveMessage() {
        return !receiveMessageQueue.isEmpty();
    }

    @Override
    public String getHostName() {
        return null;
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
        this.sendMessageQueue.push(new MessageResendOrder(range));
        this.submitToWrite();
    }

    @Override
    public void processSendMessages() {
        int time = this.processSendNumPerTime;
        try {
            while (time < 0 || time > 0 || this.sendMessageQueue.isEmpty()) {
                try {
                    MessageOrder order = this.sendMessageQueue.poll();
                    if (order == null)
                        break;
                    switch (order.getOrderType()) {
                        case SEND:
                            this.sendMessageCapsule((MessageCapsule) order);
                            break;
                        case RESEND:
                            break;
                    }
                } catch (Throwable e) {
                    LOGGER.error("processSendMessages {} ", e);
                }
            }
        } finally {
            this.writeLock.set(false);
        }
        if (this.hasSendMessage())
            this.submitToWrite();
    }

    private void sendMessageCapsule(MessageCapsule capsule) {
        if (this.sentMessageCache != null)
            this.sentMessageCache.add(capsule);
        write(capsule);
    }

    protected abstract void write(MessageCapsule capsule);

    @Override
    public void processReceiveMessage(MessageDispatcher dispatcher) {
        int time = this.processSendNumPerTime;
        try {
            while (time < 0 || time > 0 || this.receiveMessageQueue.isEmpty()) {
                try {
                    NetMessage message = this.receiveMessageQueue.poll();
                    if (message == null)
                        break;
                    int toMessage = message.getToMessage();
                    if (toMessage > 0 && this.futureHolder != null) {
                        MessageFuture<?> future = this.futureHolder.takeFuture(message.getToMessage());
                        if (future != null)
                            future.setResponse(message);
                    }
                    dispatcher.dispatch(message, this);
                } catch (Throwable e) {
                    LOGGER.error("processSendMessages {} ", e);
                }
            }
        } finally {
            this.readLock.set(false);
        }
        if (this.hasReceiveMessage())
            this.submitToRead();
    }

    private MessageFutureHolder holder() {
        if (futureHolder != null)
            return this.futureHolder;
        synchronized (this) {
            if (this.futureHolder != null)
                return this.futureHolder;
            this.futureHolder = new MessageFutureHolder();
            return this.futureHolder;
        }
    }

    @Override
    public boolean isConnect() {
        return false;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public MessageBuilderFactory getMessageBuilderFactory() {
        return null;
    }

    @Override
    public void takeFuture(int id) {

    }

}
