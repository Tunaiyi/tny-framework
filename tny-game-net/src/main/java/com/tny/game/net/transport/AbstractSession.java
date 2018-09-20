package com.tny.game.net.transport;

import com.google.common.collect.*;
import com.tny.game.common.context.*;
import com.tny.game.net.transport.message.*;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.*;

/**
 * 抽象Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public abstract class AbstractSession<UID> extends AbstractCommunicator<UID> implements NetSession<UID> {

    public static final Logger LOGGER = getLogger(AbstractSession.class);

    private volatile long id;

    /* 身份凭证 */
    protected Certificate<UID> certificate;

    /* 附加属性 */
    private Attributes attributes;

    private StampedLock sentMessageLock = new StampedLock();

    /* 发送缓存 */
    private CircularFifoQueue<Message<UID>> sentMessageQueue = null;

    @SuppressWarnings("unchecked")
    public AbstractSession(Certificate<UID> certificate, int cacheMessageSize) {
        super(MessageIdCreator.SESSION_MESSAGE_ID_MARK);
        this.certificate = certificate;
        this.id = NetAide.newSessionID();
        if (cacheMessageSize > 0)
            sentMessageQueue = new CircularFifoQueue<>(cacheMessageSize);
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
    public Certificate<UID> getCertificate() {
        return certificate;
    }

    @Override
    public List<Message<UID>> getSentMessages(Range<Long> range) {
        if (this.sentMessageQueue == null)
            return ImmutableList.of();
        StampedLock lock = this.sentMessageLock;
        long stamp = lock.readLock();
        try {
            return this.sentMessageQueue.stream()
                    .filter(e -> range.contains(e.getId()))
                    .collect(Collectors.toList());
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public Message<UID> getSentMessage(long messageId) {
        if (this.sentMessageQueue == null)
            return null;
        StampedLock lock = this.sentMessageLock;
        long stamp = lock.readLock();
        try {
            return this.sentMessageQueue.stream()
                    .filter(message -> message.getHeader().getId() == messageId)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public void addSentMessage(Message<UID> message) {
        if (this.sentMessageQueue != null) {
            StampedLock lock = this.sentMessageLock;
            long stamp = lock.writeLock();
            try {
                this.sentMessageQueue.add(message);
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    @Override
    public RespondFuture<UID> removeFuture(long messageId) {
        return super.removeFuture(messageId);
    }

    @Override
    public void registerFuture(long messageId, RespondFuture<UID> respondFuture) {
        super.registerFuture(messageId, respondFuture);
    }

}
