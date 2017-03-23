package com.tny.game.net.common.handle;


import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.SessionOutputEventHandler;
import com.tny.game.net.session.event.SessionOutputEvent;
import com.tny.game.net.session.event.SessionResendEvent;
import com.tny.game.net.session.event.SessionSendEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public class ImmediateOutputEventHandler<UID, S extends NetSession<UID>> implements SessionOutputEventHandler<UID, S> {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onOutput(S session) {
        while (!session.isInvalided() && session.hasOutputEvent()) {
            SessionOutputEvent event = session.pollOutputEvent();
            if (event != null) {
                switch (event.getEventType()) {
                    case PONG:
                    case PING:
                    case MESSAGE:
                        if (event instanceof SessionSendEvent)
                            doWrite(session, (SessionSendEvent) event);
                        break;
                    case RESEND:
                        if (event instanceof SessionResendEvent) {
                            SessionResendEvent resendEvent = (SessionResendEvent) event;
                            List<SessionSendEvent> resendEvents = session.getHandledSendEvents(resendEvent.getResendRange());
                            resendEvents.forEach(ev -> doWrite(session, ev));
                        }
                        break;

                }
            }
        }
    }

    private void doWrite(S session, SessionSendEvent event) {
        try {
            write(session, event);
        } catch (Throwable e) {
            LOGGER.error("Session [{}] {} write message exception", session.getClass(), session.getUID(), e);
        }
    }

    protected void write(S session, SessionSendEvent event) {
        session.write(event);
    }

}
