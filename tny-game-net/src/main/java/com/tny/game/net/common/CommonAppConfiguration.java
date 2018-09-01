package com.tny.game.net.common;


import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.session.*;
import com.tny.game.net.session.NetSessionHolder;

import java.io.IOException;
import java.util.List;

public class CommonAppConfiguration extends AbstractAppConfiguration {

    protected CommonAppConfiguration(String name) {
        super(name);
    }

    protected CommonAppConfiguration(String name, String path) throws IOException {
        super(name, path);
    }

    protected CommonAppConfiguration(String name, List<String> paths) throws IOException {
        super(name, paths);
    }

    public CommonAppConfiguration setSessionHolder(NetSessionHolder sessionHolder) {
        this.sessionHolder = sessionHolder;
        return this;
    }

    public CommonAppConfiguration setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        return this;
    }

    public CommonAppConfiguration setMessageBuilderFactory(MessageBuilderFactory messageBuilderFactory) {
        this.messageBuilderFactory = messageBuilderFactory;
        return this;
    }

    public CommonAppConfiguration setInputEventHandler(SessionInputEventHandler inputEventHandler) {
        this.inputEventHandler = inputEventHandler;
        return this;
    }

    public CommonAppConfiguration setOutputEventHandler(SessionOutputEventHandler outputEventHandler) {
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

    @Override
    @SuppressWarnings("unchecked")
    public NetSessionHolder getSessionHolder() {
        if (this.sessionHolder == null)
            this.sessionHolder = new CommonSessionHolder();
        return this.sessionHolder;
    }

}
