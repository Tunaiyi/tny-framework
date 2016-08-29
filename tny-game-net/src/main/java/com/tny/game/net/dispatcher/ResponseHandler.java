package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Protocol;
import com.tny.game.net.client.nio.ResponseMode;

import java.util.List;

public interface ResponseHandler<M> {

    Class<?> getMessageClass();

    ResponseMode getMonitorType();

    List<Protocol> includeProtocols();

    List<Protocol> excludeProtocols();

    void handle(Session session, Response response, M message);

}