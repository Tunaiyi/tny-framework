package com.tny.game.suite.net.spring;

import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.auth.AuthProvider;
import com.tny.game.net.common.dispatcher.CommonMessageDispatcher;
import com.tny.game.net.common.session.AbstractNetSessionHolder;
import com.tny.game.net.dispatcher.ResponseHandlerHolder;
import com.tny.game.net.plugin.PluginHolder;
import com.tny.game.net.command.MessageCommandExecutor;
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
    public void setCheckers(List<ControllerChecker> checkers) {
        super.setCheckers(checkers);
    }

    @Autowired
    @Override
    public void setAuthProviders(List<AuthProvider> authProviders) {
        super.setAuthProviders(authProviders);
    }

    @Autowired
    @Override
    public void setSessionHolder(AbstractNetSessionHolder sessionHolder) {
        super.setSessionHolder(sessionHolder);
    }

    @Autowired(required = false)
    @Override
    public void setResponseHandlerHolder(ResponseHandlerHolder responseHandlerHolder) {
        super.setResponseHandlerHolder(responseHandlerHolder);
    }

    @Autowired(required = false)
    @Override
    public KafkaAppContext setVerifier(MessageSignGenerator verifier) {
        return super.setVerifier(verifier);
    }

    @Autowired
    @Override
    public void setMessageDispatcher(CommonMessageDispatcher messageDispatcher) {
        super.setMessageDispatcher(messageDispatcher);
    }

    @Autowired
    @Override
    public void setDispatcherCommandExecutor(MessageCommandExecutor dispatcherCommandExecutor) {
        super.setDispatcherCommandExecutor(dispatcherCommandExecutor);
    }

}
