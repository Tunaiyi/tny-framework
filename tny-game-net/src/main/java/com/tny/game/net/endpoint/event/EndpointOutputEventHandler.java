package com.tny.game.net.endpoint.event;


import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

/**
 * Session 输出消息处理器
 * Created by Kun Yang on 2017/2/23.
 */
@UnitInterface
public interface EndpointOutputEventHandler<UID, E extends NetEndpoint<UID>> {

    /**
     * 处理指定endpoint的输
     *
     * @param endpoint 处理指定endpoint
     */
    void onOutput(EndpointEventsBox<UID> box, E endpoint);

}
