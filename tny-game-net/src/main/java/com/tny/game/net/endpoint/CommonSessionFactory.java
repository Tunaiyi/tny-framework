package com.tny.game.net.endpoint;

import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class CommonSessionFactory<UID> implements SessionFactory<UID, CommonSession<UID>, SessionSetting> {

    public CommonSessionFactory() {
    }

    @Override
    public CommonSession<UID> create(SessionSetting setting, EndpointEventsBoxHandler<UID, NetEndpoint<UID>> eventHandler) {
        return new CommonSession<>(eventHandler, setting.getCacheSentMessageSize());
    }

}
