package com.tny.game.net.dispatcher;


public interface MessageAction<M> {

    public void handle(Session session, int code, M body);

}
