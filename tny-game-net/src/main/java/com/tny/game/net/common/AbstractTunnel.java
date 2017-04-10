package com.tny.game.net.common;

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

/**
 * 抽象通道
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class AbstractTunnel<UID> implements NetTunnel<UID> {

    private final long id;

    protected NetSession<UID> session;

    /* 附加属性 */
    private Attributes attributes;

    public AbstractTunnel(SessionFactory<UID> sessionFactory) {
        this.id = Tunnels.newID();
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
    public boolean bind(NetSession<UID> session) {
        if (session.getCurrentTunnel() == this) {
            this.session = session;
            return true;
        }
        return false;
    }

    @Override
    public void send(MessageContent<?> content) {
        session.send(this, content);
    }

    @Override
    public void receive(Message<UID> message) {
        MessageMode mode = message.getMode();
        if (mode == MessageMode.PING)
            this.pong();
        else if (mode != MessageMode.PONG)
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

}