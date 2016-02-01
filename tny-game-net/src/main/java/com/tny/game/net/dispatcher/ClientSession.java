package com.tny.game.net.dispatcher;


public interface ClientSession extends NetSession, RequsetSession {

    public MessageFuture<?> takeFuture(int id);

    public void putFuture(MessageFuture<?> future);

    public void clearFuture();

}