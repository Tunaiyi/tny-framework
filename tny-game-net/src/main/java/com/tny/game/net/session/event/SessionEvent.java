package com.tny.game.net.session.event;


import com.tny.game.net.tunnel.Tunnel;

import java.util.Optional;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public interface SessionEvent<UID> {

    enum SessionEventType {

        MESSAGE,

        RESEND,

    }


    /**
     * @return 所属通道ID
     */
    Optional<Tunnel<UID>> getTunnel();

    /**
     * @return 事件类型
     */
    SessionEventType getEventType();

}
