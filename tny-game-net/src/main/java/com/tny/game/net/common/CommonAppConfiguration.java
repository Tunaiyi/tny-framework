package com.tny.game.net.common;


import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.MessageBuilderFactory;

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

    public CommonAppConfiguration setSessionFactory(SessionFactory<T> sessionFactory) {
        this.sessionFactory = sessionFactory;
        return this;
    }

    public CommonAppConfiguration setSessionKeeperFactory(SessionKeeperFactory sessionKeeperFactory) {
        this.sessionKeeperFactory = sessionKeeperFactory;
        return this;
    }

    public CommonAppConfiguration setMessageBuilderFactory(MessageBuilderFactory<T> messageBuilderFactory) {
        this.messageBuilderFactory = messageBuilderFactory;
        return this;
    }

    public CommonAppConfiguration setInputEventHandler(MessageInputEventHandler<T, ? extends NetTunnel<T>> inputEventHandler) {
        this.inputEventHandler = inputEventHandler;
        return this;
    }

    public CommonAppConfiguration setOutputEventHandler(MessageOutputEventHandler<T, ? extends NetTunnel<T>> outputEventHandler) {
        this.outputEventHandler = outputEventHandler;
        return this;
    }

    public CommonAppConfiguration setDispatchCommandExecutor(DispatchCommandExecutor dispatchCommandExecutor) {
        this.dispatchCommandExecutor = dispatchCommandExecutor;
        return this;
    }

    public CommonAppConfiguration setMessageDispatcher(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
        return this;
    }

    protected CommonAppConfiguration setExprHolderFactory(ExprHolderFactory exprHolderFactory) {
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
