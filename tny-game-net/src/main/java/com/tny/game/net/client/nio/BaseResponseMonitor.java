package com.tny.game.net.client.nio;

import com.tny.game.net.dispatcher.ResponseMonitor;

public abstract class BaseResponseMonitor<M> implements ResponseMonitor<M> {

    private ResponseMode monitorType;

    private Class<M> clazz;

    public BaseResponseMonitor(ResponseMode monitorType, Class<M> clazz) {
        this.monitorType = monitorType;
        this.clazz = clazz;
    }

    @Override
    public Class<?> getMessageClass() {
        return this.clazz;
    }

    @Override
    public ResponseMode getMonitorType() {
        return this.monitorType;
    }

}