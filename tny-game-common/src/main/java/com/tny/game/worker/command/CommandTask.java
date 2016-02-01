package com.tny.game.worker.command;

public interface CommandTask<T> {

    public void run();

    public Command<T> getCommand();

    public void fail(boolean excuted);

}