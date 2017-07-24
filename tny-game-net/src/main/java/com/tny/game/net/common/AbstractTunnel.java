package com.tny.game.net.common;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.ResendMessage;
import com.tny.game.net.session.Session;
import com.tny.game.net.session.SessionFactory;
import com.tny.game.net.tunnel.NetTunnel;
import com.tny.game.net.tunnel.Tunnels;

import java.util.Collection;
import java.util.Set;

/**
 * 抽象通道
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class AbstractTunnel<UID> implements NetTunnel<UID> {

    private final long id;

    private long latestActiveAt;

    protected NetSession<UID> session;

    /* 附加属性 */
    private Attributes attributes;

    /* 接收排除消息模型 */
    private Set<MessageMode> receiveExcludes = ImmutableSet.of();

    /* 发送排除消息模型 */
    private Set<MessageMode> sendExcludes = ImmutableSet.of();

    public AbstractTunnel(SessionFactory<UID> sessionFactory) {
        this.id = Tunnels.newID();
        this.latestActiveAt = System.currentTimeMillis();
        this.session = sessionFactory.createSession(this);
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public UID getUID() {
        return session.getUID();
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
    public long getLatestActiveAt() {
        return latestActiveAt;
    }

    @Override
    public boolean bind(NetSession<UID> session) {
        if (session.getCurrentTunnel() == this) {
            this.session = session;
            return true;
        }
        return false;
    }

    @Override
    public void send(MessageContent<?> content) {
        this.latestActiveAt = System.currentTimeMillis();
        session.send(this, content);
    }

    @Override
    public void receive(Message<UID> message) {
        this.latestActiveAt = System.currentTimeMillis();
        session.receive(this, message);
    }

    @Override
    public void resend(ResendMessage message) {
        session.resend(this, message);
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
    public Session<UID> getSession() {
        return session;
    }

    @Override
    public void receiveExcludes(Collection<MessageMode> modes) {
        this.receiveExcludes = ImmutableSet.copyOf(modes);
    }

    @Override
    public void sendExcludes(Collection<MessageMode> modes) {
        this.sendExcludes = ImmutableSet.copyOf(modes);
    }

    @Override
    public Collection<MessageMode> getReceiveExcludes() {
        return this.receiveExcludes;
    }

    @Override
    public Collection<MessageMode> getSendExcludes() {
        return this.sendExcludes;
    }
}
