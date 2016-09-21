package com.tny.game.net.dispatcher;


import com.tny.game.common.result.ResultCode;

public interface MessageAction<M> {

    void handle(Session session, Request request, ResultCode code, M body);

}
