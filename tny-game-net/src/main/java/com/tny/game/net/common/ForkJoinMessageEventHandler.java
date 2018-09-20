package com.tny.game.net.common;

import com.tny.game.common.concurrent.ForkJoinPools;
import com.tny.game.common.config.Config;
import com.tny.game.common.context.*;
import com.tny.game.common.worker.command.Command;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.utils.NetConfigs.*;

/**
 * Created by Kun Yang on 2017/3/18.
 */
@Unit("ForkJoinMessageEventHandler")
public class ForkJoinMessageEventHandler<UID, T extends NetTunnel<UID>> implements MessageInputEventHandler<UID, T>, MessageOutputEventHandler<UID, T> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinMessageEventHandler.class);

    private static final String POOL_NAME = "SessionEventHandlerThread";

    private AppConfiguration appConfiguration;

    private ForkJoinPool forkJoinPool;

    private final static AttrKey<AtomicBoolean> HANDLE_INPUT_STATE = AttrKeys.key(ForkJoinMessageEventHandler.class, "HANDLE_INPUT_STATE");
    private final static AttrKey<AtomicBoolean> HANDLE_OUTPUT_STATE = AttrKeys.key(ForkJoinMessageEventHandler.class, "HANDLE_OUTPUT_STATE");

    public ForkJoinMessageEventHandler(AppConfiguration appConfiguration) {
        Config config = appConfiguration.getProperties();
        this.appConfiguration = appConfiguration;
        this.forkJoinPool = ForkJoinPools.pool(config.getInt(SESSION_EXECUTOR_THREADS, SESSION_EXECUTOR_DEFAULT_THREADS), POOL_NAME);
    }

    private AtomicBoolean getHandleState(AttrKey<AtomicBoolean> key, T tunnel) {
        AtomicBoolean state = tunnel.attributes().getAttribute(key);
        if (state == null) {
            synchronized (tunnel) {
                state = tunnel.attributes().getAttribute(key);
                if (state != null)
                    return state;
                state = new AtomicBoolean(false);
                tunnel.attributes().setAttribute(key, state);
            }
        }
        return state;
    }

    @Override
    public void onInput(T tunnel) {
        AtomicBoolean handled = getHandleState(HANDLE_INPUT_STATE, tunnel);
        if (handled.get())
            return;
        tryHandle(tunnel, handled, MessageEventsBox::isHasInputEvent, this::doInput);
    }

    @Override
    public void onOutput(T tunnel) {
        AtomicBoolean handled = getHandleState(HANDLE_OUTPUT_STATE, tunnel);
        if (handled.get())
            return;
        if (handled.compareAndSet(false, true)) {
            forkJoinPool.submit(() -> tryHandle(tunnel, handled, MessageEventsBox::isHasOutputEvent, this::doOutput));
        }
    }

    private void tryHandle(T tunnel, AtomicBoolean handled, Predicate<MessageEventsBox<UID>> isHasEvent, BiConsumer<AtomicBoolean, T> handler) {
        if (handled.get())
            return;
        while (isHasEvent.test(tunnel.getEventsBox())) {
            if (handled.compareAndSet(false, true)) {
                handler.accept(handled, tunnel);
            } else {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doInput(AtomicBoolean handled, T tunnel) {
        try {
            MessageEventsBox<UID> eventsBox = tunnel.getEventsBox();
            while (eventsBox.isHasInputEvent()) {
                MessageInputEvent event = eventsBox.pollInputEvent();
                if (event instanceof MessageReceiveEvent) {
                    try {
                        handleMessageReceiveEvent(as(event));
                    } catch (Throwable e) {
                        LOGGER.error("", e);
                    }
                }
            }
        } finally {
            handled.set(false);
        }
    }

    private void handleMessageReceiveEvent(MessageReceiveEvent<UID> event) {
        MessageDispatcher messageDispatcher = appConfiguration.getMessageDispatcher();
        DispatchCommandExecutor commandExecutor = appConfiguration.getDispatchCommandExecutor();
        Message<UID> message = event.getMessage();
        NetTunnel<UID> tunnel = event.getTunnel();
        if (tunnel != null) {
            try {
                Command command = messageDispatcher.dispatch(event);
                commandExecutor.submit(tunnel, command);
            } catch (DispatchException e) {
                if (message.getMode() == MessageMode.REQUEST)
                    tunnel.send(MessageContext.toResponse(message.getHeader(), e.getResultCode()));
                LOGGER.error("#ThreadPoolSessionInputEvetHandler#处理InputEvent异常", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doOutput(AtomicBoolean handled, T tunnel) {
        MessageEventsBox<UID> eventsBox = tunnel.getEventsBox();
        try {
            while (eventsBox.isHasOutputEvent()) {
                MessageOutputEvent<UID> event = eventsBox.pollOutputEvent();
                if (event instanceof MessageSendEvent) {
                    handleMessageSendEvent(tunnel, as(event));
                    return;
                }
                if (event instanceof MessageResendEvent) {
                    handleMessageResendEvent(tunnel, as(event));
                    return;
                }
            }
        } finally {
            handled.set(false);
        }
    }

    private void handleMessageResendEvent(T tunnel, MessageResendEvent<UID> event) {
        Optional<NetSession<UID>> sessionOpt = tunnel.getSession();
        if (!sessionOpt.isPresent())
            event.resendFailed(new SessionException("{} is not bind session", tunnel));
        sessionOpt.ifPresent(session -> {
            List<Message<UID>> messages = session.getSentMessages(event.getResendRange());
            for (Message<UID> message : messages) {
                try {
                    tunnel.write(message, null);
                } catch (Throwable e) {
                    LOGGER.warn("{} resend exception", tunnel, e);
                }
            }
            event.resendSuccess(tunnel, messages);
        });
    }

    private void handleMessageSendEvent(T tunnel, MessageSendEvent<UID> event) {
        try {
            MessageSendEvent<UID> sendEvent = as(event);
            Message<UID> message = tunnel.getMessageBuilderFactory().newBuilder()
                    .setContent(sendEvent.getMessageContext())
                    .setCertificate(tunnel.getCertificate())
                    .build();
            tunnel.write(message, event);
        } catch (Exception e) {
            LOGGER.error("doOutputMessage", e);
            event.sendFail(e);
        }
    }

}
