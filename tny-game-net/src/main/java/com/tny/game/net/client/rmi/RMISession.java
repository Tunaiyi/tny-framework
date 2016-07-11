package com.tny.game.net.client.rmi;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.dispatcher.AbstractNetSession;
import com.tny.game.net.dispatcher.MessageAction;
import com.tny.game.net.dispatcher.MessageBuilderFactory;
import com.tny.game.net.dispatcher.MessageFuture;
import com.tny.game.net.dispatcher.message.simple.SimpleMessageBuilderFactory;
import io.netty.channel.ChannelFuture;

import java.util.List;
import java.util.Optional;

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
    protected MessageBuilderFactory getMessageBuilderFactory() {
        return super.getMessageBuilderFactory();
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
    public ChannelFuture request(Protocol protocol, Object... params) {
        return null;
    }

    @Override
    public <B> ChannelFuture request(Protocol protocol, MessageAction<B> action, Object... params) {
        return null;
    }

    @Override
    public Optional<ChannelFuture> response(Protocol protocol, Object body) {
        return null;
    }

    @Override
    public Optional<ChannelFuture> response(Protocol protocol, ResultCode code, Object body) {
        return null;
    }

    @Override
    public <B> ChannelFuture request(Protocol protocol, MessageFuture<B> future, Object... params) {
        return null;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public List<RequestChecker> getCheckers() {
        return ImmutableList.of();
    }
}