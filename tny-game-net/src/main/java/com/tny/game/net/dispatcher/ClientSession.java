package com.tny.game.net.dispatcher;


public interface ClientSession extends NetSession, RequestSession {

    MessageFuture<?> takeFuture(int id);

    void putFuture(MessageFuture<?> future);

    void clearFuture();

}