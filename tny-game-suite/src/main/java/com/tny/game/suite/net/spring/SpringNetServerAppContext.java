package com.tny.game.suite.net.spring;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.NetServerAppContext;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.config.ServerConfig;
import com.tny.game.net.config.ServerConfigFactory;
import com.tny.game.net.auth.AuthProvider;
import com.tny.game.net.dispatcher.ChannelServerSessionFactory;
import com.tny.game.net.message.MessageDispatcher;
import com.tny.game.net.dispatcher.NetMessageDispatcher;
import com.tny.game.net.common.session.BaseNetSessionHolder;
import com.tny.game.net.dispatcher.ResponseHandlerHolder;
import com.tny.game.net.plugin.PluginHolder;
import com.tny.game.net.command.MessageCommandExecutor;
import io.netty.channel.Channel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * 默认服务器上下文对象
 * Created by Kun Yang on 16/1/27.
 */
@Component
@Profile({GAME, SERVER})
public class SpringNetServerAppContext implements NetServerAppContext, ApplicationContextAware {

    private Attributes Attributes = ContextAttributes.create();

    private ServerConfig serverConfig;

    @Autowired
    private ServerConfigFactory serverConfigFactory;

    @Autowired
    private ChannelMaker<Channel> channelMaker;

    @Autowired
    private PluginHolder pluginHolder;

    @Autowired
    private BaseNetSessionHolder sessionHolder;

    @Autowired
    private ChannelServerSessionFactory sessionFactory;

    @Autowired
    private NetMessageDispatcher messageDispatcher;

    @Autowired
    private MessageCommandExecutor dispatcherCommandExecutor;

    @Autowired(required = false)
    private ResponseHandlerHolder responseHandlerHolder;

    private AtomicBoolean init = new AtomicBoolean(false);

    private List<AuthProvider> authProviders;

    private List<ControllerChecker> checkers;

    private ApplicationContext applicationContext;

    @Override
    public String getScopeType() {
        return serverConfig.getScopeType();
    }

    @Override
    public void initContext(Consumer<AppContext> accepter) {
        if (this.init.compareAndSet(false, true)) {
            this.serverConfig = serverConfigFactory.getServerContext();
            this.messageDispatcher.initDispatcher(this);
            this.getMessageDispatcher();
            this.getAuthProviders();
            Map<String, AuthProvider> providerMap = applicationContext.getBeansOfType(AuthProvider.class);
            this.authProviders = ImmutableList.copyOf(providerMap.values());
            Map<String, ControllerChecker> checkerMap = applicationContext.getBeansOfType(ControllerChecker.class);
            this.checkers = ImmutableList.copyOf(checkerMap.values());
            if (accepter != null)
                accepter.accept(this);
        }
    }

    @Override
    public Attributes attr() {
        return this.Attributes;
    }

    @Override
    public ChannelMaker<Channel> getChannelMaker() {
        return this.channelMaker;
    }

    @Override
    public PluginHolder getPluginHolder() {
        return this.pluginHolder;
    }

    @Override
    public List<AuthProvider> getAuthProviders() {
        return this.authProviders;
    }

    @Override
    public BaseNetSessionHolder getSessionHolder() {
        return this.sessionHolder;
    }

    @Override
    public ChannelServerSessionFactory getSessionFactory() {
        return this.sessionFactory;
    }


    @Override
    public MessageDispatcher getMessageDispatcher() {
        return this.messageDispatcher;
    }

    @Override
    public MessageCommandExecutor getCommandExecutor() {
        return this.dispatcherCommandExecutor;
    }

    @Override
    public ResponseHandlerHolder getResponseHandlerHolder() {
        return responseHandlerHolder;
    }

    @Override
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    @Override
    public List<ControllerChecker> getControllerCheckers() {
        return this.checkers;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}