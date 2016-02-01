package com.tny.game.net.client.rmi;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.dispatcher.AbstractNetSession;
import com.tny.game.net.dispatcher.MessageAction;
import com.tny.game.net.dispatcher.MessageBuilderFactory;
import com.tny.game.net.dispatcher.MessageFuture;
import com.tny.game.net.dispatcher.message.simple.SimpleMessageBuilderFactory;
import io.netty.channel.ChannelFuture;

public class RMISession extends AbstractNetSession {

    private static final MessageBuilderFactory DEF_MSG_BUILDER_FACTORY = new SimpleMessageBuilderFactory();

    private String host;

    public RMISession(long userId, String userGroup, String host) {
        super(LoginCertificate.createLogin(userId, userGroup));
        this.host = host;
        this.messageBuilderFactory = RMISession.DEF_MSG_BUILDER_FACTORY;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public String getHostName() {
        return this.host;
    }

    @Override
    public boolean isConnect() {
        return true;
    }

    @Override
    public ChannelFuture requset(Protocol protocol, Object... params) {
        return null;
    }

    @Override
    public <B> ChannelFuture requset(Protocol protocol, MessageAction<B> action, Object... params) {
        return null;
    }

    @Override
    public ChannelFuture response(Protocol protocol, Object body) {
        return null;
    }

    @Override
    public ChannelFuture response(Protocol protocol, ResultCode code, Object body) {
        return null;
    }

    @Override
    public <B> ChannelFuture requset(Protocol protocol, MessageFuture<B> future, Object... params) {
        return null;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

}