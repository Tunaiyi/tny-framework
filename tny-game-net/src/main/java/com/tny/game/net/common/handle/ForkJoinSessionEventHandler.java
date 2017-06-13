package com.tny.game.net.common.handle;

import com.tny.game.common.config.Config;
import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrKeys;
import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.AppConstants;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.common.dispatcher.MessageDispatcher;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.MessageMode;
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
import com.tny.game.net.tunnel.TunnelContent;
import com.tny.game.worker.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * Created by Kun Yang on 2017/3/18.
 */
@Unit("ForkJoinSessionEventHandler")
public class ForkJoinSessionEventHandler<UID, S extends NetSession<UID>> implements SessionInputEventHandler<UID, S>, SessionOutputEventHandler<UID, S> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinSessionEventHandler.class);

    private AppConfiguration appConfiguration;

    private ForkJoinPool forkJoinPool;

    private final static AttrKey<AtomicBoolean> HANDLE_INPUT_STATE = AttrKeys.key(ForkJoinSessionEventHandler.class, "HANDLE_INPUT_STATE");
    private final static AttrKey<AtomicBoolean> HANDLE_OUTPUT_STATE = AttrKeys.key(ForkJoinSessionEventHandler.class, "HANDLE_OUTPUT_STATE");

    public ForkJoinSessionEventHandler(AppConfiguration appConfiguration) {
        Config config = appConfiguration.getProperties();
        this.appConfiguration = appConfiguration;
        this.forkJoinPool = new ForkJoinPool(
                config.getInt(AppConstants.SESSION_EXECUTOR_THREADS, Runtime.getRuntime().availableProcessors() * 2),
                new CoreThreadFactory("SessionEventHandlerThread"),
                null, false);
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
        // AtomicInteger handledID = session.attributes().getAttribute(HANDLED_MSG_ID);
        // if (handledID == null)
        //     session.attributes().setAttribute(HANDLED_MSG_ID, handledID = new AtomicInteger(0));
        try {
            Command command = messageDispatcher.dispatch(message, event.getMessageFuture(), event.getTunnel());
            commandExecutor.submit(session, command);
        } catch (DispatchException e) {
            if (message.getMode() == MessageMode.REQUEST)
                session.send(MessageContent.toResponse(message, e.getResultCode(), message.getID()));
            if (event.hasMessageFuture())
                event.getMessageFuture().completeExceptionally(e);
            LOGGER.error("#ThreadPoolSessionInputEvetHandler#处理InputEvent异常", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void doOutput(AtomicBoolean handled, S session) {
        try {
            while (session.hasOutputEvent()) {
                SessionOutputEvent event = session.pollOutputEvent();
                if (event != null) {
                    try {
                        switch (event.getEventType()) {
                            case MESSAGE:
                                if (event instanceof SessionSendEvent)
                                    doWrite((TunnelContent<UID>) event, event.getTunnel());
                                break;
                            case RESEND:
                                if (event instanceof SessionResendEvent) {
                                    SessionResendEvent resendEvent = (SessionResendEvent) event;
                                    List<SessionSendEvent<UID>> resendEvents = session.getHandledSendEvents(resendEvent.getResendRange());
                                    resendEvents.forEach(ev -> doWrite(ev, resendEvent.getTunnel()));
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

    private void doWrite(TunnelContent<UID> content, Tunnel<UID> tunnel) {
        try {
            if (tunnel == null)
                content.cancelSendWait(true);
            write(content, tunnel);
        } catch (Throwable e) {
            content.cancelSendWait(true);
            LOGGER.error("Tunnel [{}] write message exception", tunnel, e);
        }
    }

    protected void write(TunnelContent<UID> content, Tunnel<UID> tunnel) {
        if (tunnel instanceof NetTunnel) {
            ((NetTunnel<UID>) tunnel).write(content);
        } else {
            content.cancelSendWait(true);
        }
    }

}
