package com.tny.game.net.base;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.dispatcher.AuthProvider;
import com.tny.game.net.dispatcher.MessageDispatcher;
import com.tny.game.net.dispatcher.AbstractNetSessionHolder;
import com.tny.game.net.dispatcher.ResponseHandlerHolder;
import com.tny.game.net.dispatcher.plugin.PluginHolder;
import com.tny.game.net.executor.DispatcherCommandExecutor;

import java.util.List;
import java.util.function.Consumer;

public interface AppContext {

    String getScopeType();

    void initContext(Consumer<AppContext> accepter);

    Attributes attr();

    PluginHolder getPluginHolder();

    List<AuthProvider> getAuthProviders();

    List<ControllerChecker> getControllerCheckers();

    AbstractNetSessionHolder getSessionHolder();

    MessageDispatcher getMessageDispatcher();

    DispatcherCommandExecutor getCommandExecutor();

    ResponseHandlerHolder getResponseHandlerHolder();
}
