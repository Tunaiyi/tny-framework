package com.tny.game.net.dispatcher.listener;

public abstract class MessageDispatcherListenerAdapter implements MessageDispatcherListener {

    @Override
    public void execute(ExecuteMessageEvent event) {
    }

    @Override
    public void executeException(DispatchMessageErrorEvent event) {
    }

    @Override
    public void executeFinish(ExecuteMessageEvent event) {
    }

    @Override
    public void executeDispatchException(DispatchExceptionEvent event) {
    }

}
