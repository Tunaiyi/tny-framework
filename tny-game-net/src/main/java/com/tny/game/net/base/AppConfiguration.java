package com.tny.game.net.base;

import com.tny.game.common.config.Config;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.common.dispatcher.MessageDispatcher;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.session.SessionFactory;
import com.tny.game.net.session.SessionInputEventHandler;
import com.tny.game.net.session.SessionOutputEventHandler;
import com.tny.game.net.session.holder.NetSessionHolder;

public interface AppConfiguration extends AppContext {

    Config getProperties();

    NetSessionHolder getSessionHolder();

    SessionFactory getSessionFactory();

    MessageBuilderFactory getMessageBuilderFactory();

    SessionOutputEventHandler getOutputEventHandler();

    SessionInputEventHandler getInputEventHandler();

    DispatchCommandExecutor getDispatchCommandExecutor();

    MessageDispatcher getMessageDispatcher();

    MessageSignGenerator getMessageSignGenerator();

}
