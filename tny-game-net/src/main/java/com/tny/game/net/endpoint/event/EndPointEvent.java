package com.tny.game.net.endpoint.event;


import com.tny.game.net.transport.*;

import java.util.Optional;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public interface EndPointEvent<UID> {

    /**
     * @return 所属通道ID
     */
    Optional<NetTunnel<UID>> getTunnel();

    /**
     * @return 事件类型
     */
    EndpointEventType getEventType();

}
