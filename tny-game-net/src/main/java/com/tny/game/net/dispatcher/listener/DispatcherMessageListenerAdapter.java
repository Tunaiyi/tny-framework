package com.tny.game.net.dispatcher.listener;

public abstract class DispatcherMessageListenerAdapter implements DispatcherMessageListener {

    @Override
    public void execute(DispatcherMessageEvent event) {
    }

    @Override
    public void executeException(DispatcherMessageErrorEvent event) {
    }

    @Override
    public void finish(DispatcherMessageEvent event) {
    }

    @Override
    public void executeDispatchException(DispatchExceptionEvent event) {
    }

}
