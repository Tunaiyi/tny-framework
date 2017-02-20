package com.tny.game.suite.net.spring;

import com.tny.game.net.checker.MessageChecker;
import com.tny.game.net.checker.RequestVerifier;
import com.tny.game.net.dispatcher.AuthProvider;
import com.tny.game.net.dispatcher.NetMessageDispatcher;
import com.tny.game.net.dispatcher.NetSessionHolder;
import com.tny.game.net.dispatcher.ResponseHandlerHolder;
import com.tny.game.net.dispatcher.plugin.PluginHolder;
import com.tny.game.net.executor.DispatcherCommandExecutor;
import com.tny.game.net.kafka.KafkaAppContext;
import com.tny.game.net.kafka.KafkaMessageBuilderFactory;
import com.tny.game.net.kafka.KafkaTicketTaker;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Kun Yang on 16/8/10.
 */
public class SpringKafkaAppContext extends KafkaAppContext {

    public SpringKafkaAppContext(String scopeType) {
        super(scopeType);
    }

    @Autowired
    @Override
    public KafkaAppContext setTicketTaker(KafkaTicketTaker ticketTaker) {
        return super.setTicketTaker(ticketTaker);
    }

    @Autowired
    @Override
    public KafkaAppContext setMessageBuilderFactory(KafkaMessageBuilderFactory messageBuilderFactory) {
        return super.setMessageBuilderFactory(messageBuilderFactory);
    }

    @Autowired
    @Override
    public void setPluginHolder(PluginHolder pluginHolder) {
        super.setPluginHolder(pluginHolder);
    }

    @Autowired
    @Override
    public void setCheckers(List<MessageChecker> checkers) {
        super.setCheckers(checkers);
    }

    @Autowired
    @Override
    public void setAuthProviders(List<AuthProvider> authProviders) {
        super.setAuthProviders(authProviders);
    }

    @Autowired
    @Override
    public void setSessionHolder(NetSessionHolder sessionHolder) {
        super.setSessionHolder(sessionHolder);
    }

    @Autowired(required = false)
    @Override
    public void setResponseHandlerHolder(ResponseHandlerHolder responseHandlerHolder) {
        super.setResponseHandlerHolder(responseHandlerHolder);
    }

    @Autowired(required = false)
    @Override
    public KafkaAppContext setVerifier(RequestVerifier verifier) {
        return super.setVerifier(verifier);
    }

    @Autowired
    @Override
    public void setMessageDispatcher(NetMessageDispatcher messageDispatcher) {
        super.setMessageDispatcher(messageDispatcher);
    }

    @Autowired
    @Override
    public void setDispatcherCommandExecutor(DispatcherCommandExecutor dispatcherCommandExecutor) {
        super.setDispatcherCommandExecutor(dispatcherCommandExecutor);
    }

}
