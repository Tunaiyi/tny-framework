package com.tny.game.net.base;

import com.tny.game.common.config.Config;
import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.MessageBuilderFactory;

public interface AppConfiguration<T> extends AppContext {

    T getDefaultUserId();

    Config getProperties();

    SessionKeeperFactory getSessionKeeperFactory();

    SessionFactory<T> getSessionFactory();

    MessageBuilderFactory<T> getMessageBuilderFactory();

    MessageOutputEventHandler<T, NetTunnel<T>> getOutputEventHandler();

    MessageInputEventHandler<T, NetTunnel<T>> getInputEventHandler();

    DispatchCommandExecutor getDispatchCommandExecutor();

    MessageDispatcher getMessageDispatcher();

    ExprHolderFactory getExprHolderFactory();

}
