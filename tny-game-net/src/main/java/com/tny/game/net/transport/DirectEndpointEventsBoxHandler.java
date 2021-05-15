package com.tny.game.net.transport;

import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.listener.*;
import org.slf4j.*;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public class DirectEndpointEventsBoxHandler<UID, E extends NetEndpoint<UID>> extends AbstractEndpointEventsBoxHandler<UID, E> {

    public static final Logger LOGGER = LoggerFactory.getLogger(DirectEndpointEventsBoxHandler.class);

    public DirectEndpointEventsBoxHandler() {
    }

    public DirectEndpointEventsBoxHandler(EndpointEventHandlerSetting setting) {
        super(setting);
    }

    public DirectEndpointEventsBoxHandler(EndpointEventHandlerSetting setting, MessageHandler<UID> messageHandler) {
        super(setting, messageHandler);
    }

    @Override
    protected void execute(EndpointEventFlow flow, Runnable handle) {
        handle.run();
    }

}
