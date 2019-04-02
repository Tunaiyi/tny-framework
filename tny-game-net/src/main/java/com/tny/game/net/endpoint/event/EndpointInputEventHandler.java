package com.tny.game.net.endpoint.event;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

/**
 * Session 输入消息处理器
 * Created by Kun Yang on 2017/2/23.
 */
@UnitInterface
public interface EndpointInputEventHandler<UID, E extends NetEndpoint<UID>> {

    /**
     * 处理指定session的输入
     *
     * @param session 处理指定session
     */
    void onInput(EndpointEventsBox<UID> box, E session);

}
