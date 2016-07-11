package com.tny.game.net.base.simple;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.coder.SimpleChannelMaker;
import com.tny.game.net.dispatcher.AuthProvider;
import com.tny.game.net.dispatcher.DefaultMessageDispatcher;
import com.tny.game.net.dispatcher.DefaultSessionHolder;
import com.tny.game.net.dispatcher.MessageDispatcher;
import com.tny.game.net.dispatcher.NetMessageDispatcher;
import com.tny.game.net.dispatcher.NetSessionHolder;
import com.tny.game.net.dispatcher.ServerSessionFactory;
import com.tny.game.net.dispatcher.plugin.DefaultPluginHolder;
import com.tny.game.net.dispatcher.plugin.PluginHolder;
import com.tny.game.net.executor.DispatcherCommandExecutor;
import com.tny.game.net.executor.normal.ThreadPoolCommandExecutor;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class AbstractAppContext implements AppContext {

    private Attributes Attributes = ContextAttributes.create();

    private ChannelMaker<Channel> channelMaker;

    private PluginHolder pluginHolder;

    private List<RequestChecker> checkers;

    private List<AuthProvider> authProviders = new ArrayList<>();

    private NetSessionHolder sessionHolder;

    private ServerSessionFactory sessionFactory;

    private NetMessageDispatcher messageDispatcher;

    private DispatcherCommandExecutor dispatcherCommandExecutor;

    private AtomicBoolean init = new AtomicBoolean(false);

    @Override
    public void initContext(Consumer<AppContext> accepter) {
        if (this.init.compareAndSet(false, true)) {
            this.messageDispatcher.initDispatcher(this);
            this.getMessageDispatcher();
            this.getAuthProviders();
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
        if (this.channelMaker == null)
            this.channelMaker = new SimpleChannelMaker<>(this.checkers);
        return this.channelMaker;
    }

    @Override
    public PluginHolder getPluginHolder() {
        if (this.pluginHolder == null)
            this.pluginHolder = new DefaultPluginHolder();
        return this.pluginHolder;
    }

    public List<RequestChecker> getCheckers() {
        return this.checkers;
    }

    @Override
    public List<AuthProvider> getAuthProviders() {
        if (this.authProviders == null)
            throw new NullPointerException("authProvider is null");
        return this.authProviders;
    }

    @Override
    public NetSessionHolder getSessionHolder() {
        if (this.sessionHolder == null)
            this.sessionHolder = new DefaultSessionHolder();
        return this.sessionHolder;
    }

    @Override
    public ServerSessionFactory getSessionFactory() {
        if (this.sessionFactory == null)
            this.sessionFactory = this.getDefaultSessionFactory();
        return this.sessionFactory;
    }

    protected abstract ServerSessionFactory getDefaultSessionFactory();

    @Override
    public MessageDispatcher getMessageDispatcher() {
        if (this.messageDispatcher == null)
            this.messageDispatcher = new DefaultMessageDispatcher(true);
        return this.messageDispatcher;
    }

    @Override
    public DispatcherCommandExecutor getCommandExecutor() {
        if (this.dispatcherCommandExecutor == null)
            this.dispatcherCommandExecutor = new ThreadPoolCommandExecutor();
        return this.dispatcherCommandExecutor;
    }

    public void setChannelMaker(ChannelMaker<Channel> channelMaker) {
        this.channelMaker = channelMaker;
    }

    public void setPluginHolder(PluginHolder pluginHolder) {
        this.pluginHolder = pluginHolder;
    }

    public void setCheckers(List<RequestChecker> checkers) {
        this.checkers = checkers;
    }

    public void setAuthProviders(List<AuthProvider> authProviders) {
        this.authProviders = authProviders;
    }

    public void setSessionHolder(NetSessionHolder sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

    public void setSessionFactory(ServerSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setMessageDispatcher(NetMessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    public void setDispatcherCommandExecutor(DispatcherCommandExecutor dispatcherCommandExecutor) {
        this.dispatcherCommandExecutor = dispatcherCommandExecutor;
    }

}
