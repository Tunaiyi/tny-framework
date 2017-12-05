package com.tny.game.net.common.session.handle;

import com.tny.game.common.config.Config;
import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrKeys;
import com.tny.game.common.thread.ForkJoinPools;
import com.tny.game.common.utils.Logs;
import com.tny.game.common.worker.command.Command;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.exception.NetException;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.message.MessageWriteFuture;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.event.SessionInputEvent;
import com.tny.game.net.session.event.SessionInputEventHandler;
import com.tny.game.net.session.event.SessionOutputEvent;
import com.tny.game.net.session.event.SessionOutputEventHandler;
import com.tny.game.net.session.event.SessionReceiveEvent;
import com.tny.game.net.session.event.SessionResendEvent;
import com.tny.game.net.session.event.SessionSendEvent;
import com.tny.game.net.tunnel.NetTunnel;
import com.tny.game.net.tunnel.Tunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static com.tny.game.common.utils.ObjectAide.*;
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

    private AtomicBoolean getInputHandleState(AttrKey<AtomicBoolean> key, S session) {
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
        tryHandle(HANDLE_INPUT_STATE, session, this::doInput);
    }

    @Override
    public void onOutput(S session) {
        tryHandle(HANDLE_OUTPUT_STATE, session, this::doOutput);
    }

    @SuppressWarnings("unchecked")
    private void doInput(AtomicBoolean handled, S session) {
        try {
            while (session.hasInputEvent()) {
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
        AtomicBoolean handled = getInputHandleState(key, session);
        if (handled.get())
            return;
        if (handled.compareAndSet(false, true)) {
            forkJoinPool.submit(() -> consumer.accept(handled, session));
        }
    }

    private void handleMessageEvent(S session, SessionReceiveEvent<UID> event) {
        MessageDispatcher messageDispatcher = appConfiguration.getMessageDispatcher();
        DispatchCommandExecutor commandExecutor = appConfiguration.getDispatchCommandExecutor();
        Message<?> message = event.getMessage();
        Tunnel<UID> tunnel = event.getTunnel().orElse(null);
        if (tunnel != null) {
            try {
                Command command = messageDispatcher.dispatch(message, event.getMessageFuture(), tunnel);
                commandExecutor.submit(session, command);
            } catch (DispatchException e) {
                if (message.getMode() == MessageMode.REQUEST)
                    session.send(MessageContent.toResponse(message, e.getResultCode(), message.getID()));
                if (event.hasMessageFuture())
                    event.getMessageFuture().completeExceptionally(e);
                LOGGER.error("#ThreadPoolSessionInputEvetHandler#处理InputEvent异常", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doOutput(AtomicBoolean handled, S session) {
        try {
            while (session.hasOutputEvent()) {
                SessionOutputEvent<UID> event = session.pollOutputEvent();
                if (event != null) {
                    try {
                        switch (event.getEventType()) {
                            case MESSAGE:
                                if (event instanceof SessionSendEvent) {
                                    SessionSendEvent<UID> sendEvent = as(event);
                                    if (session.isClosed()) {
                                        NetException exception = new NetException("session is close");
                                        sendEvent.fail(exception);
                                    } else {
                                        Optional<Tunnel<UID>> tunnelOpt = sendEvent.getTunnel();
                                        if (tunnelOpt.isPresent()) {
                                            Tunnel<UID> tunnel = tunnelOpt.get();
                                            Message<UID> message = session.createMessage(tunnel, sendEvent.getContent());
                                            try {
                                                write(tunnel, message, sendEvent);
                                            } catch (Throwable e) {
                                                sendEvent.fail(e);
                                                return;
                                            }
                                        } else {
                                            NetException exception = new NetException("tunnel is close");
                                            sendEvent.fail(exception);
                                        }
                                    }
                                }
                                break;
                            case RESEND:
                                if (event instanceof SessionResendEvent) {
                                    SessionResendEvent<UID> resendEvent = (SessionResendEvent) event;
                                    if (session.isClosed()) {
                                        NetException exception = new NetException("session is close");
                                        resendEvent.resendFail(exception);
                                    } else {
                                        Tunnel<UID> tunnel = resendEvent.getTunnel().orElse(null);
                                        if (tunnel != null) {
                                            List<Message<UID>> resendEvents = session.getHandledSendEvents(resendEvent.getResendRange());
                                            for (Message<UID> message : resendEvents) {
                                                try {
                                                    write(tunnel, message, null);
                                                } catch (Throwable e) {
                                                    resendEvent.resendFail(e);
                                                    return;
                                                }
                                            }
                                            resendEvent.resendSuccess(tunnel);
                                        } else {
                                            NetException exception = new NetException("tunnel is close");
                                            resendEvent.resendFail(exception);
                                        }
                                    }
                                }
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


    protected void write(Tunnel<UID> tunnel, Message<UID> message, MessageWriteFuture<UID> future) throws Throwable {
        if (tunnel instanceof NetTunnel) {
            ((NetTunnel<UID>) tunnel).write(message, future);
        } else {
            throw new NetException(Logs.format("未支持 {} Tunnel", tunnel.getClass()));
        }
    }

}
