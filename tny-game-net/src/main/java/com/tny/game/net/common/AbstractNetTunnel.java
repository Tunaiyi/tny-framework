package com.tny.game.net.common;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.context.*;
import com.tny.game.net.exception.TunnelWriteException;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.net.tunnel.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 抽象通道
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class AbstractNetTunnel<UID> implements NetTunnel<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractNetTunnel.class);

    private static final AtomicLong TUNNEL_ID_CREATOR = new AtomicLong(0);

    private static long newID() {
        return TUNNEL_ID_CREATOR.incrementAndGet();
    }

    private final long id;

    private long lastReadAt;
    protected long lastWriteAt;

    protected NetSession<UID> session;

    /* 附加属性 */
    private Attributes attributes;

    /* 接收排除消息模型 */
    private Set<MessageMode> receiveExcludes = ImmutableSet.of();

    /* 发送排除消息模型 */
    private Set<MessageMode> sendExcludes = ImmutableSet.of();

    private MessageBuilderFactory<UID> messageBuilderFactory;

    protected AbstractNetTunnel() {
        this.id = newID();
        long now = System.currentTimeMillis();
        this.lastReadAt = now;
        this.lastWriteAt = now;
    }

    protected void init(SessionFactory<UID> sessionFactory, MessageBuilderFactory<UID> messageBuilderFactory) {
        this.session = sessionFactory.createSession(this);
        this.messageBuilderFactory = messageBuilderFactory;
    }

    @Override
    public NetSession<UID> getSession() {
        return session;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public UID getUid() {
        return session.getUid();
    }

    @Override
    public String getUserGroup() {
        return this.session.getUserGroup();
    }

    @Override
    public boolean isLogin() {
        return session.isLogin();
    }

    @Override
    public long getLastReadAt() {
        return lastReadAt;
    }

    @Override
    public long getLastWriteAt() {
        return lastWriteAt;
    }

    @Override
    public MessageBuilderFactory<UID> getMessageBuilderFactory() {
        return messageBuilderFactory;
    }

    @Override
    public boolean bind(NetSession<UID> session) {
        if (session.getCurrentTunnel() == this) {
            this.session = session;
            return true;
        }
        return false;
    }

    // @Override
    // public void send(MessageContent<UID> content) {
    //     session.send(this, content);
    //     if (!isClosed())
    //         this.lastWriteAt = System.currentTimeMillis();
    // }
    //
    // @Override
    // public void receive(Message<UID> message) {
    //     session.receive(this, message);
    //     if (!isClosed())
    //         this.lastReadAt = System.currentTimeMillis();
    // }
    //
    // @Override
    // public void resend(ResendMessage<UID> message) {
    //     session.resend(this, message);
    //     if (!isClosed())
    //         this.lastReadAt = System.currentTimeMillis();
    // }

    @Override
    public void write(Message<UID> message, WriteCallback<UID> callback) throws TunnelWriteException {
        if (!isClosed())
            this.lastWriteAt = System.currentTimeMillis();
        doWrite(message, callback);
    }

    @Override
    public void ping() {
        if (!isClosed()) {
            this.lastWriteAt = System.currentTimeMillis();
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
            this.lastWriteAt = System.currentTimeMillis();
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
    public void receiveExcludes(Collection<MessageMode> modes) {
        this.receiveExcludes = ImmutableSet.copyOf(modes);
    }

    @Override
    public void sendExcludes(Collection<MessageMode> modes) {
        this.sendExcludes = ImmutableSet.copyOf(modes);
    }

    @Override
    public boolean isReceiveExclude(MessageMode mode) {
        return this.receiveExcludes.contains(mode);
    }

    @Override
    public boolean isSendExclude(MessageMode mode) {
        return this.sendExcludes.contains(mode);
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
