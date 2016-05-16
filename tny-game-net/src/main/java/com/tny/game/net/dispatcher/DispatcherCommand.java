package com.tny.game.net.dispatcher;

import com.tny.game.worker.Callback;
import com.tny.game.worker.command.Command;

public interface DispatcherCommand<T> extends Command {

    T invoke();

    long getUserID();

    int getProtocol();

    String getName();

    /**
     * 获取命令所属Session
     *
     * @return
     */
    Session getSession();


    void setCallback(Callback<?> callback);

}