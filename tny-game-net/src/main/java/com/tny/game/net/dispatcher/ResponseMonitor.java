package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Protocol;
import com.tny.game.net.client.nio.ResponseMode;

import java.util.List;

public interface ResponseMonitor<M> {

    public Class<?> getMessageClass();

    public ResponseMode getMonitorType();

    public List<Protocol> includeController();

    public List<Protocol> excludeController();

    public void handle(Session session, Response response, M message);

}