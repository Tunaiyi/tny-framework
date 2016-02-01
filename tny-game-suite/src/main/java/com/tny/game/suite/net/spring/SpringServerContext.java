package com.tny.game.suite.net.spring;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.ServerContext;
import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.config.ServerConfig;
import com.tny.game.net.config.ServerConfigFactory;
import com.tny.game.net.dispatcher.*;
import com.tny.game.net.dispatcher.plugin.PluginHolder;
import com.tny.game.net.executor.DispatcherCommandExecutor;
import io.netty.channel.Channel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 默认服务器上下文对象
 * Created by Kun Yang on 16/1/27.
 */
@Component
@Profile({"suite.server", "suite.all"})
public class SpringServerContext implements ServerContext, ApplicationContextAware {

    private Attributes Attributes = ContextAttributes.create();

    private ServerConfig serverConfig;

    @Autowired
    private ServerConfigFactory serverConfigFactory;

    @Autowired
    private ChannelMaker<Channel> channelMaker;

    @Autowired
    private PluginHolder pluginHolder;

    @Autowired
    private NetSessionHolder sessionHolder;

    @Autowired
    private ServerSessionFactory sessionFactory;

    @Autowired
    private NetMessageDispatcher messageDispatcher;

    @Autowired
    private DispatcherCommandExecutor dispatcherCommandExecutor;

    private AtomicBoolean init = new AtomicBoolean(false);

    private List<AuthProvider> authProviders;

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
            List<AuthProvider> providers = new ArrayList<>(providerMap.values());
            authProviders = Collections.unmodifiableList(providers);
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
    public NetSessionHolder getSessionHolder() {
        return this.sessionHolder;
    }

    @Override
    public ServerSessionFactory getSessionFactory() {
        return this.sessionFactory;
    }


    @Override
    public MessageDispatcher getMessageDispatcher() {
        return this.messageDispatcher;
    }

    @Override
    public DispatcherCommandExecutor getCommandExecutor() {
        return this.dispatcherCommandExecutor;
    }

    @Override
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}