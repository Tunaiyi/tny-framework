package com.tny.game.net.base;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.auth.AuthProvider;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.command.MessageCommandExecutor;
import com.tny.game.net.command.MessageDispatcher;
import com.tny.game.net.command.ThreadPoolMessageCommandExecutor;
import com.tny.game.net.common.dispatcher.CommonMessageDispatcher;
import com.tny.game.net.common.dispatcher.DefaultMessageDispatcher;
import com.tny.game.net.plugin.DefaultPluginHolder;
import com.tny.game.net.plugin.PluginHolder;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.holder.DefaultSessionHolder;
import com.tny.game.net.session.holder.NetSessionHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class AbstractAppContext implements AppContext {

    private Attributes Attributes = ContextAttributes.create();

    private PluginHolder pluginHolder;

    private List<ControllerChecker> checkers = new ArrayList<>();

    private List<AuthProvider> authProviders = new ArrayList<>();

    private NetSessionHolder sessionHolder;

    private CommonMessageDispatcher messageDispatcher;

    private MessageCommandExecutor dispatcherCommandExecutor;

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
    public PluginHolder getPluginHolder() {
        if (this.pluginHolder == null)
            this.pluginHolder = new DefaultPluginHolder();
        return this.pluginHolder;
    }

    @Override
    public List<ControllerChecker> getControllerCheckers() {
        return this.checkers;
    }

    @Override
    public List<AuthProvider> getAuthProviders() {
        if (this.authProviders == null)
            throw new NullPointerException("authProvider is null");
        return this.authProviders;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NetSessionHolder<?, NetSession<?>> getSessionHolder() {
        if (this.sessionHolder == null)
            this.sessionHolder = new DefaultSessionHolder<>(6000L);
        return this.sessionHolder;
    }


    @Override
    public MessageDispatcher getMessageDispatcher() {
        if (this.messageDispatcher == null)
            this.messageDispatcher = new DefaultMessageDispatcher();
        return this.messageDispatcher;
    }

    @Override
    public MessageCommandExecutor getCommandExecutor() {
        if (this.dispatcherCommandExecutor == null)
            this.dispatcherCommandExecutor = new ThreadPoolMessageCommandExecutor();
        return this.dispatcherCommandExecutor;
    }

    public void setPluginHolder(PluginHolder pluginHolder) {
        this.pluginHolder = pluginHolder;
    }

    public void setCheckers(List<ControllerChecker> checkers) {
        this.checkers = checkers;
    }

    public void setAuthProviders(List<AuthProvider> authProviders) {
        this.authProviders = authProviders;
    }

    public void setSessionHolder(NetSessionHolder sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

    public void setMessageDispatcher(CommonMessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    public void setDispatcherCommandExecutor(MessageCommandExecutor dispatcherCommandExecutor) {
        this.dispatcherCommandExecutor = dispatcherCommandExecutor;
    }

}
