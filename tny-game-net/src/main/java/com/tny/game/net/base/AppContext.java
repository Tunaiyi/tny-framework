package com.tny.game.net.base;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.auth.AuthProvider;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.command.MessageCommandExecutor;
import com.tny.game.net.message.MessageDispatcher;
import com.tny.game.net.plugin.PluginHolder;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.holder.NetSessionHolder;

import java.util.List;
import java.util.function.Consumer;

public interface AppContext {

    String getScopeType();

    void initContext(Consumer<AppContext> accepter);

    Attributes attr();

    PluginHolder getPluginHolder();

    List<AuthProvider> getAuthProviders();

    List<ControllerChecker> getControllerCheckers();

    NetSessionHolder<?, NetSession<?>> getSessionHolder();

    MessageDispatcher getMessageDispatcher();

    MessageCommandExecutor getCommandExecutor();

    // ResponseHandlerHolder getResponseHandlerHolder();
}
