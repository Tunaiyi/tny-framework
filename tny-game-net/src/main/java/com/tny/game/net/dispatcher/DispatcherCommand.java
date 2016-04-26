package com.tny.game.net.dispatcher;

public interface DispatcherCommand<T> {

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

}