package com.tny.game.net.base;

import com.tny.game.common.config.Config;
import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.command.executor.DispatchCommandExecutor;
import com.tny.game.net.message.*;

public interface AppConfiguration<T> extends AppContext {

    T getDefaultUserId();

    Config getProperties();

    MessageFactory<T> getMessageFactory();

    MessageHandler<T> getMessageHandler();

    DispatchCommandExecutor getDispatchCommandExecutor();

    MessageDispatcher getMessageDispatcher();

    ExprHolderFactory getExprHolderFactory();

}
