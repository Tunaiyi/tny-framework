package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/13 8:48 下午
 */
@FunctionalInterface
public interface EndpointEventsBoxProcessor<UID, E extends NetEndpoint<UID>> {

    void handler(EndpointEventsBox<UID> box, E endpoint, AtomicBoolean handling);

}
