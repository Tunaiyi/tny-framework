package com.tny.game.net.session;


import com.tny.game.net.tunnel.*;

import java.util.Optional;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public interface MessageEvent<UID> {

    enum SessionEventType {

        MESSAGE,

        RESEND,

    }


    /**
     * @return 所属通道ID
     */
    Optional<NetTunnel<UID>> getTunnel();

    /**
     * @return 事件类型
     */
    SessionEventType getEventType();

}
