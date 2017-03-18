package com.tny.game.net.message;


import com.tny.game.common.result.ResultCode;
import com.tny.game.net.session.Session;

public interface MessageAction<M> {

    void handle(Session session, Message message, ResultCode code, M body);

}
