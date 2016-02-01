package com.tny.game.net.dispatcher.listener;

public abstract class DispatcherRequestListenerAdapter implements DispatcherRequestListener {

    @Override
    public void execute(DispatcherRequestEvent event) {
    }

    @Override
    public void executeException(DispatcherRequestErrorEvent event) {
    }

    @Override
    public void finish(DispatcherRequestEvent event) {
    }

    @Override
    public void executeDispatchException(DispatchExceptionEvent event) {
    }

}
