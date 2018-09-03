package com.tny.game.net.base;

import com.tny.game.common.config.Config;
import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.session.*;
import com.tny.game.net.tunnel.NetTunnel;

public interface AppConfiguration extends AppContext {

    Config getProperties();

    NetSessionHolder getSessionHolder();

    <T> SessionFactory<T> getSessionFactory();

    <T> MessageBuilderFactory<T> getMessageBuilderFactory();

    <T, S extends NetTunnel<T>> MessageOutputEventHandler<T, S> getOutputEventHandler();

    <T, S extends NetTunnel<T>> MessageInputEventHandler<T, S> getInputEventHandler();

    DispatchCommandExecutor getDispatchCommandExecutor();

    MessageDispatcher getMessageDispatcher();

    ExprHolderFactory getExprHolderFactory();

}
