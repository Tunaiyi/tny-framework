package com.tny.game.net.session.event;

import com.tny.game.net.message.Message;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public interface SessionEvent {

    enum SessionEventType {

        PING,

        PONG,

        MESSAGE,

        RESEND,

    }

    Message<?> getMessage();

    SessionEventType getEventType();


}
