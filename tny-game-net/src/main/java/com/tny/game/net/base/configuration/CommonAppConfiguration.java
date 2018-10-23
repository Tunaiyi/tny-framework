package com.tny.game.net.base.configuration;


import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.command.executor.DispatchCommandExecutor;
import com.tny.game.net.message.*;

import java.io.IOException;
import java.util.List;

public abstract class CommonAppConfiguration<T> extends AbstractAppConfiguration<T> {

    protected CommonAppConfiguration(String name, T defaultUserId) {
        super(name, defaultUserId);

    }

    protected CommonAppConfiguration(String name, T defaultUserId, String path) throws IOException {
        super(name, defaultUserId, path);
    }

    protected CommonAppConfiguration(String name, T defaultUserId, List<String> paths) throws IOException {
        super(name, defaultUserId, paths);
    }


    public CommonAppConfiguration<T> setMessageBuilderFactory(MessageFactory<T> messageBuilderFactory) {
        this.messageBuilderFactory = messageBuilderFactory;
        return this;
    }


    protected CommonAppConfiguration<T> setMessageHandler(MessageHandler<T> messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }

    public CommonAppConfiguration<T> setDispatchCommandExecutor(DispatchCommandExecutor dispatchCommandExecutor) {
        this.dispatchCommandExecutor = dispatchCommandExecutor;
        return this;
    }

    public CommonAppConfiguration<T> setMessageDispatcher(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
        return this;
    }

    public CommonAppConfiguration setExprHolderFactory(ExprHolderFactory exprHolderFactory) {
        this.exprHolderFactory = exprHolderFactory;
        return this;
    }

    // @Override
    // @SuppressWarnings("unchecked")
    // public NetSessionKeeper getSessionKeeperFactory() {
    //     if (this.sessionHolder == null)
    //         this.sessionHolder = new CommonSessionKeeper();
    //     return this.sessionHolder;
    // }

}
