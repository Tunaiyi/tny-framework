package com.tny.game.net.session;

import com.google.common.collect.*;
import com.tny.game.common.context.*;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.NetTunnel;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.*;

/**
 * 抽象Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public abstract class AbstractSession<UID> implements NetSession<UID> {

    public static final Logger LOGGER = getLogger(AbstractSession.class);

    private volatile long id;

    /* 身份凭证 */
    protected NetCertificate<UID> certificate;

    private volatile AtomicInteger messageIDCounter = new AtomicInteger(0);

    /* 附加属性 */
    private Attributes attributes;

    protected MessageInputEventHandler<UID, NetTunnel<UID>> inputEventHandler;

    protected MessageOutputEventHandler<UID, NetTunnel<UID>> outputEventHandler;

    private StampedLock sentMessageLock = new StampedLock();

    /* 发送缓存 */
    private CircularFifoQueue<Message<UID>> sentMessageQueue = null;

    @SuppressWarnings("unchecked")
    public AbstractSession(UID unloginUID, MessageInputEventHandler<UID, NetTunnel<UID>> inputEventHandler, MessageOutputEventHandler<UID, NetTunnel<UID>> outputEventHandler, int cacheMessageSize) {
        this.init(unloginUID, inputEventHandler, outputEventHandler, cacheMessageSize);
    }

    protected void init(UID unloginUID, MessageInputEventHandler<UID, NetTunnel<UID>> inputEventHandler, MessageOutputEventHandler<UID, NetTunnel<UID>> outputEventHandler, int cacheMessageSize) {
        this.id = SessionAide.newSessionID();
        this.certificate = NetCertificate.createUnLogin(unloginUID);
        this.outputEventHandler = outputEventHandler;
        this.inputEventHandler = inputEventHandler;
        if (cacheMessageSize > 0)
            sentMessageQueue = new CircularFifoQueue<>(cacheMessageSize);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public UID getUid() {
        return certificate.getUserId();
    }

    @Override
    public String getUserGroup() {
        return certificate.getUserGroup();
    }

    @Override
    public boolean isLogin() {
        return this.certificate.isLogin();
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
    public NetCertificate<UID> getCertificate() {
        return certificate;
    }

    @Override
    public List<Message<UID>> getSentMessages(Range<Integer> range) {
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
    public Message<UID> getSentMessageByToID(int messageId) {
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

    protected int createMessageID() {
        return messageIDCounter.incrementAndGet();
    }

    protected void addSentMessage(Message<UID> message) {
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

}
