package com.tny.game.net.client.rmi;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.message.Protocol;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.dispatcher.AbstractNetSession;
import com.tny.game.net.message.MessageAction;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.session.MessageFuture;
import com.tny.game.net.dispatcher.MessageSendFuture;
import com.tny.game.net.message.simple.SimpleMessageBuilderFactory;

import java.util.List;
import java.util.Optional;

public class RMISession extends AbstractNetSession {

    private static final MessageBuilderFactory DEF_MSG_BUILDER_FACTORY = new SimpleMessageBuilderFactory();

    private String host;

    public RMISession(long userId, String userGroup, String host) {
        super(LoginCertificate.createLogin(System.currentTimeMillis(), userId, userGroup));
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
    public Optional<MessageSendFuture> request(Protocol protocol, Object... params) {
        return Optional.empty();
    }

    @Override
    public Optional<MessageSendFuture> request(Protocol protocol, MessageAction<?> action, Object... params) {
        return Optional.empty();
    }

    @Override
    public Optional<MessageSendFuture> request(Protocol protocol, MessageAction<?> action, long timeout, Object... params) {
        return Optional.empty();
    }

    @Override
    public Optional<MessageSendFuture> request(Protocol protocol, MessageFuture<?> future, Object... params) {
        return Optional.empty();
    }

    @Override
    public Optional<MessageSendFuture> response(Protocol protocol, ResultCode code, Object body) {
        return Optional.empty();
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public List<ControllerChecker> getCheckers() {
        return ImmutableList.of();
    }
}