package com.tny.game.net.transport;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.event.*;

/**
 * Created by Kun Yang on 2017/3/18.
 */
@UnitInterface
public interface EndpointEventsBoxHandler<UID, E extends NetEndpoint<UID>>
        extends EndpointInputEventHandler<UID, E>, EndpointOutputEventHandler<UID, E> {

}
