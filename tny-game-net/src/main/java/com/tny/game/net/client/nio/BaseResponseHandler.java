package com.tny.game.net.client.nio;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.message.Protocol;
import com.tny.game.net.dispatcher.ResponseHandler;

import java.util.List;

public abstract class BaseResponseHandler<M> implements ResponseHandler<M> {

    private MessageMode monitorType;

    private Class<M> clazz;

    private List<Protocol> includes;

    private List<Protocol> excludes;

    public BaseResponseHandler(MessageMode monitorType, Class<M> clazz) {
        this(monitorType, clazz, null, null);
    }

    public BaseResponseHandler(MessageMode monitorType, Class<M> clazz, Protocol... includes) {
        this(monitorType, clazz, ImmutableList.copyOf(includes), null);
    }

    public BaseResponseHandler(MessageMode monitorType, Class<M> clazz, List<Protocol> includes, List<Protocol> excludes) {
        this.monitorType = monitorType;
        this.clazz = clazz;
        if (includes == null || includes.isEmpty())
            this.includes = ImmutableList.of();
        else
            this.includes = ImmutableList.copyOf(includes);
        if (excludes == null || excludes.isEmpty())
            this.excludes = ImmutableList.of();
        else
            this.excludes = ImmutableList.copyOf(excludes);
    }


    @Override
    public List<Protocol> excludeProtocols() {
        return excludes;
    }

    @Override
    public List<Protocol> includeProtocols() {
        return includes;
    }

    @Override
    public Class<?> getMessageClass() {
        return this.clazz;
    }

    @Override
    public MessageMode getMonitorType() {
        return this.monitorType;
    }

}