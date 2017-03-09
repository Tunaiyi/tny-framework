package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Protocol;
import com.tny.game.net.base.MessageMode;

import java.util.List;

public interface ResponseHandler<M> {

    Class<?> getMessageClass();

    MessageMode getMonitorType();

    List<Protocol> includeProtocols();

    List<Protocol> excludeProtocols();

    void handle(Session session, Response response, M message);

}