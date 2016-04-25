package com.tny.game.net.dispatcher;

import com.tny.game.net.dispatcher.command.UserCommand;

public interface DispatcherCommand<M> extends UserCommand<M> {

    /**
     * 获取命令所属Session
     *
     * @return
     */
    Session getSession();

}