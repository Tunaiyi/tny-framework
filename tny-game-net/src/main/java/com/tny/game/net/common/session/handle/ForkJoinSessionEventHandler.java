package com.tny.game.net.common.session.handle;

import com.tny.game.common.concurrent.ForkJoinPools;
import com.tny.game.common.config.Config;
import com.tny.game.common.context.*;
import com.tny.game.common.worker.command.Command;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.net.tunnel.NetTunnel;
import org.slf4j.*;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static com.tny.game.net.utils.NetConfigs.*;

/**
 * Created by Kun Yang on 2017/3/18.
 */
@Unit("ForkJoinSessionEventHandler")
public class ForkJoinSessionEventHandler<UID, S extends NetSession<UID>> implements SessionInputEventHandler<UID, S>, SessionOutputEventHandler<UID, S> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinSessionEventHandler.class);

    private static final String POOL_NAME = "SessionEventHandlerThread";

    private AppConfiguration appConfiguration;

    private ForkJoinPool forkJoinPool;

    private final static AttrKey<AtomicBoolean> HANDLE_INPUT_STATE = AttrKeys.key(ForkJoinSessionEventHandler.class, "HANDLE_INPUT_STATE");
    private final static AttrKey<AtomicBoolean> HANDLE_OUTPUT_STATE = AttrKeys.key(ForkJoinSessionEventHandler.class, "HANDLE_OUTPUT_STATE");

    public ForkJoinSessionEventHandler(AppConfiguration appConfiguration) {
        Config config = appConfiguration.getProperties();
        this.appConfiguration = appConfiguration;
        this.forkJoinPool = ForkJoinPools.pool(config.getInt(SESSION_EXECUTOR_THREADS, SESSION_EXECUTOR_DEFAULT_THREADS), POOL_NAME);
    }

    private AtomicBoolean getHandleState(AttrKey<AtomicBoolean> key, S session) {
        AtomicBoolean state = session.attributes().getAttribute(key);
        if (state == null) {
            synchronized (session) {
                state = session.attributes().getAttribute(key);
                if (state != null)
                    return state;
                state = new AtomicBoolean(false);
                session.attributes().setAttribute(key, state);
            }
        }
        return state;
    }

    @Override
    public void onInput(S session) {
        AtomicBoolean handled = getHandleState(HANDLE_INPUT_STATE, session);
        if (handled.get())
            return;
        if (handled.compareAndSet(false, true)) {
            doInput(handled, session);
        }
    }

    @Override
    public void onOutput(S session) {
        AtomicBoolean handled = getHandleState(HANDLE_OUTPUT_STATE, session);
        if (handled.get())
            return;
        if (handled.compareAndSet(false, true)) {
            forkJoinPool.submit(() -> doOutput(handled, session));
        }
    }

    @SuppressWarnings("unchecked")
    private void doInput(AtomicBoolean handled, S session) {
        try {
            while (session.isHasInputEvent()) {
                SessionInputEvent event = session.pollInputEvent();
                if (event != null) {
                    try {
                        switch (event.getEventType()) {
                            case MESSAGE:
                                handleMessageEvent(session, (SessionReceiveEvent<UID>) event);
                        }
                    } catch (Throwable e) {
                        LOGGER.error("", e);
                    }
                }
            }
        } finally {
            handled.set(false);
        }
    }

    private void tryHandle(AttrKey<AtomicBoolean> key, S session, BiConsumer<AtomicBoolean, S> consumer) {
        AtomicBoolean handled = getHandleState(key, session);
        if (handled.get())
            return;
        if (handled.compareAndSet(false, true)) {
            forkJoinPool.submit(() -> consumer.accept(handled, session));
        }
    }

    private void handleMessageEvent(S session, SessionReceiveEvent<UID> event) {
        MessageDispatcher messageDispatcher = appConfiguration.getMessageDispatcher();
        DispatchCommandExecutor commandExecutor = appConfiguration.getDispatchCommandExecutor();
        Message<UID> message = event.getMessage();
        NetTunnel<UID> tunnel = event.getTunnel().orElse(null);
        if (tunnel != null) {
            if (message.getMode() == MessageMode.RESPONSE)
                event.completeResponse();
            try {
                Command command = messageDispatcher.dispatch(message, tunnel);
                commandExecutor.submit(session, command);
            } catch (DispatchException e) {
                if (message.getMode() == MessageMode.REQUEST)
                    session.send(MessageContent.toResponse(message.getHeader(), e.getResultCode()));
                LOGGER.error("#ThreadPoolSessionInputEvetHandler#处理InputEvent异常", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doOutput(AtomicBoolean handled, S session) {
        try {
            while (session.isHasOutputEvent()) {
                SessionOutputEvent<UID> event = session.pollOutputEvent();
                if (event != null) {
                    try {
                        switch (event.getEventType()) {
                            case MESSAGE:
                                if (event instanceof SessionSendEvent)
                                    doOutputMessage(session, (SessionSendEvent<UID>) event);
                                break;
                            case RESEND:
                                if (event instanceof SessionResendEvent)
                                    doOutputResend(session, (SessionResendEvent<UID>) event);
                                break;
                            default:
                                break;
                        }
                    } catch (Throwable e) {
                        LOGGER.error("", e);
                    }
                }
            }
        } finally {
            handled.set(false);
        }
    }

    private void doOutputMessage(S session, SessionSendEvent<UID> event) {
        try {
            if (session.isClosed()) {
                throw new NetException("session is close");
            } else {
                session.write(event);
            }
        } catch (Exception e) {
            LOGGER.error("doOutputMessage", e);
            event.sendFail(e);
        }
    }

    private void doOutputResend(S session, SessionResendEvent<UID> event) {
        if (session.isClosed()) {
            NetException exception = new NetException("session is close");
            event.resendFailed(exception);
        } else {
            NetTunnel<UID> tunnel = event.getTunnel().orElse(null);
            if (tunnel != null) {
                try {
                    List<Message<UID>> resendMessages = session.getSentMessages(event.getResendRange());
                    for (Message<UID> message : resendMessages) {
                        tunnel.write(message, null);
                    }
                    event.resendSuccess(tunnel, resendMessages);
                } catch (Throwable e) {
                    event.resendFailed(e);
                }
            } else {
                event.resendFailed(new NetException("tunnel is close"));
            }
        }
    }


}
