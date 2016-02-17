package com.tny.game.net.dispatcher;

import com.tny.game.net.dispatcher.command.UserCommand;
import com.tny.game.worker.command.CommandTask;

public interface DispatcherCommand<M> extends UserCommand<M>, CommandTask<M> {

    /**
     * 获取命令所属Session
     *
     * @return
     */
    Session getSession();

}