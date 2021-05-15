package com.tny.game.net.transport;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.listener.*;

import java.util.concurrent.ForkJoinPool;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public class ForkJoinEndpointEventsBoxHandler<UID, E extends NetEndpoint<UID>> extends AbstractEndpointEventsBoxHandler<UID, E> {

    private ForkJoinPool forkJoinPool;

    public ForkJoinEndpointEventsBoxHandler() {
    }

    public ForkJoinEndpointEventsBoxHandler(EndpointEventHandlerSetting setting) {
        super(setting);
    }

    public ForkJoinEndpointEventsBoxHandler(EndpointEventHandlerSetting setting, MessageHandler<UID> messageHandler) {
        super(setting, messageHandler);
    }

    @Override
    protected void execute(EndpointEventFlow flow, Runnable handle) {
        if (flow == EndpointEventFlow.INPUT) {
            handle.run();
        } else {
            this.forkJoinPool.execute(handle);
        }

    }

    @Override
    public void prepareStart() {
        super.prepareStart();
        this.forkJoinPool = ForkJoinPools.pool(this.setting.getThreads(), this.getClass().getSimpleName(), true);
    }

}
