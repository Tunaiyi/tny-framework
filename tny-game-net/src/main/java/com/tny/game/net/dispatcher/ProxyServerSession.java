package com.tny.game.net.dispatcher;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.coder.DataPacketEncoder;
import io.netty.channel.ChannelFuture;

public class ProxyServerSession implements ServerSession {

    private ServerSession session;

    public ProxyServerSession(ServerSession session) {
        this.session = session;
    }

    @Override
    public long getUID() {
        return this.session.getUID();
    }

    @Override
    public String getGroup() {
        return this.session.getGroup();
    }

    @Override
    public long getLoginAt() {
        return this.session.getLoginAt();
    }

    @Override
    public boolean isAskerLogin() {
        return this.session.isAskerLogin();
    }

    @Override
    public String getHostName() {
        return this.session.getHostName();
    }

    @Override
    public Attributes attributes() {
        return this.session.attributes();
    }

    @Override
    public boolean isConnect() {
        return this.session.isConnect();
    }

    @Override
    public MessageBuilderFactory getMessageBuilderFactory() {
        return this.session.getMessageBuilderFactory();
    }

    @Override
    public DataPacketEncoder getEncoder() {
        return this.session.getEncoder();
    }

    @Override
    public RequestChecker getChecker() {
        return this.session.getChecker();
    }

    @Override
    public void login(LoginCertificate loginInfo) {
        this.session.login(loginInfo);
    }

    @Override
    public void disconnect() {
        this.session.disconnect();
    }

    @Override
    public LoginCertificate getCertificate() {
        return this.session.getCertificate();
    }

    public Session setSession(ServerSession session) {
        Session old = this.session;
        this.session = session;
        return old;
    }

    @Override
    public boolean isOnline() {
        return this.session.isOnline();
    }

    @Override
    public ChannelFuture response(Protocol protocol, Object body) {
        return this.session.response(protocol, body);
    }

    @Override
    public ChannelFuture response(Protocol protocol, ResultCode code, Object body) {
        return this.session.response(protocol, code, body);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.session == null) ? 0 : this.session.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.session == obj)
            return true;
        return false;
    }

}
