package com.tny.game.net.transport;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.context.*;
import com.tny.game.common.utils.Throws;
import com.tny.game.net.base.NetResultCode;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.message.*;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.*;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.transport.SessionEvents.*;
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

    /* 状态 */
    protected SessionState state = SessionState.OFFLINE;

    /* 离线时间 */
    protected volatile long offlineTime;

    /* 附加属性 */
    private Attributes attributes;

    /* 发送缓存 */
    private Deque<Message<UID>> sentMessageQueue = null;

    /* 缓存数量 */
    private SessionConfigurer configurer;

    /* 缓存队列锁 */
    private ReadWriteLock cacheLock;

    private MessageIdCreator idCreator = new MessageIdCreator(MessageIdCreator.SESSION_MESSAGE_ID_MARK);

    public AbstractSession(Certificate<UID> certificate, SessionConfigurer configurer) {
        super(MessageIdCreator.SESSION_MESSAGE_ID_MARK);
        this.certificate = certificate;
        this.id = NetAide.newSessionID();
        this.configurer = configurer;
        if (configurer.getMaxCacheMessageSize() > 0) {
            sentMessageQueue = new ConcurrentLinkedDeque<>();
            cacheLock = new ReentrantReadWriteLock();
        }
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
    public SessionState getState() {
        return state;
    }

    @Override
    public boolean isOnline() {
        return this.state == SessionState.ONLINE;
    }

    @Override
    public boolean isOffline() {
        return this.state == SessionState.OFFLINE;
    }

    @Override
    public boolean isClosed() {
        return this.state == SessionState.CLOSE;
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
    public List<Message<UID>> getSentMessages(long from, long to) {
        if (this.sentMessageQueue == null)
            return ImmutableList.of();
        Lock lock = this.cacheLock.readLock();
        lock.lock();
        try {
            List<Message<UID>> messages = new ArrayList<>();
            boolean start = from < 0;
            for (Message<UID> message : this.sentMessageQueue) {
                if (!start) {
                    if (message.getId() == from)
                        start = true;
                    else
                        continue;
                }
                messages.add(message);
                if (message.getId() == to)
                    break;
            }
            return messages;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Message<UID> getSentMessage(long messageId) {
        if (this.sentMessageQueue == null)
            return null;
        Lock lock = this.cacheLock.readLock();
        lock.lock();
        try {
            return this.sentMessageQueue.stream()
                    .filter(message -> message.getHeader().getId() == messageId)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addSentMessage(Message<UID> message) {
        if (this.sentMessageQueue != null) {
            this.sentMessageQueue.add(message);
            int maxCacheMessageSize = configurer.getMaxCacheMessageSize();
            if (this.sentMessageQueue.size() <= maxCacheMessageSize)
                return;
            Lock lock = this.cacheLock.writeLock();
            if (lock.tryLock()) {
                try {
                    while (this.sentMessageQueue.size() > maxCacheMessageSize)
                        this.sentMessageQueue.poll();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    @Override
    public SendContext<UID> sendAsyn(MessageSubject subject, MessageContext<UID> messageContext) {
        try {
            select(subject, messageContext).sendAsyn(subject, messageContext);
        } catch (Throwable e) {
            completeExceptionally(messageContext, e);
            LOGGER.warn(e.getMessage());
        }
        return messageContext;
    }

    @Override
    public SendContext<UID> sendSync(MessageSubject subject, MessageContext<UID> messageContext, long timeout) throws NetException {
        try {
            return select(subject, messageContext).sendSync(subject, messageContext, timeout);
        } catch (NetException e) {
            completeExceptionally(messageContext, e);
            throw e;
        } catch (Throwable e) {
            completeExceptionally(messageContext, e);
            throw new NetException(e);
        }
    }

    private Tunnel<UID> select(MessageSubject subject, MessageContext<UID> messageContext) throws SessionException {
        if (isClosed())
            throw new SessionException("Session [{}] 已经关闭, 无法发送", this);
        Tunnel<UID> tunnel = selectTunnel(subject, messageContext);
        if (tunnel == null)
            throw new NullPointerException(format("{} is no tunnel", this));
        return tunnel;
    }

    protected abstract Tunnel<UID> selectTunnel(MessageSubject subject, MessageContext<UID> messageContext);

    @Override
    public RespondFuture<UID> removeFuture(long messageId) {
        return super.removeFuture(messageId);
    }

    @Override
    public void registerFuture(long messageId, RespondFuture<UID> respondFuture) {
        super.registerFuture(messageId, respondFuture);
    }

    @Override
    public long createMessageId() {
        return this.idCreator.createId();
    }

    @Override
    public void acceptTunnel(NetTunnel<UID> tunnel) throws ValidatorFailException {
        Throws.checkNotNull(tunnel, "newSession is null");
        Certificate<UID> newCertificate = tunnel.getCertificate();
        if (!newCertificate.isAutherized()) {
            throw new ValidatorFailException(NetResultCode.UNLOGIN);
        }
        if (!certificate.isSameCertificate(newCertificate)) { // 是否是同一个授权
            throw new ValidatorFailException(format("Certificate new [{}] 与 old [{}] 不同", newCertificate, this.certificate));
        }
        if (this.state == SessionState.CLOSE) // 判断 session 状态是否可以重登
            throw new ValidatorFailException(NetResultCode.SESSION_LOSS);
        synchronized (this) {
            if (this.state == SessionState.CLOSE) // 判断 session 状态是否可以重登
                throw new ValidatorFailException(NetResultCode.SESSION_LOSS);
            NetTunnel<UID> current = doAcceptTunnel(tunnel);
            if (current != null) {
                this.offlineTime = 0;
                this.state = SessionState.ONLINE;
                ON_ACCEPT.notify(this, current);
            }
        }
    }

    @Override
    public long getOfflineTime() {
        return offlineTime;
    }

    protected abstract NetTunnel<UID> doAcceptTunnel(NetTunnel<UID> newTunnel) throws ValidatorFailException;

    void setOffline() {
        this.offlineTime = System.currentTimeMillis();
        this.state = SessionState.OFFLINE;
        ON_OFFLINE.notify(this);
    }

    void setClose() {
        this.state = SessionState.CLOSE;
        this.destroyFutureHolder();
        ON_CLOSE.notify(this);
    }


}
