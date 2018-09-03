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
import com.tny.game.net.tunnel.*;
import org.slf4j.*;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.net.utils.NetConfigs.*;

/**
 * Created by Kun Yang on 2017/3/18.
 */
@Unit("ForkJoinSessionEventHandler")
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
        if (handled.compareAndSet(false, true)) {
            doInput(handled, tunnel);
        }
    }

    @Override
    public void onOutput(T tunnel) {
        AtomicBoolean handled = getHandleState(HANDLE_OUTPUT_STATE, tunnel);
        if (handled.get())
            return;
        if (handled.compareAndSet(false, true)) {
            forkJoinPool.submit(() -> {
                doOutput(handled, tunnel);
                if (tunnel.getEventsBox().isHasOutputEvent())
                    onOutput(tunnel);
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void doInput(AtomicBoolean handled, T tunnel) {
        try {
            MessageEventsBox<UID> eventsBox = tunnel.getEventsBox();
            while (eventsBox.isHasInputEvent()) {
                MessageInputEvent event = eventsBox.pollInputEvent();
                if (event != null) {
                    try {
                        switch (event.getEventType()) {
                            case MESSAGE:
                                handleMessageEvent((MessageReceiveEvent<UID>) event);
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

    private void handleMessageEvent(MessageReceiveEvent<UID> event) {
        MessageDispatcher messageDispatcher = appConfiguration.getMessageDispatcher();
        DispatchCommandExecutor commandExecutor = appConfiguration.getDispatchCommandExecutor();
        Message<UID> message = event.getMessage();
        NetTunnel<UID> tunnel = event.getTunnel().orElse(null);
        if (tunnel != null) {
            NetSession<UID> session = tunnel.getSession();
            if (message.getMode() == MessageMode.RESPONSE)
                event.completeResponse();
            try {
                Command command = messageDispatcher.dispatch(message, tunnel);
                commandExecutor.submit(tunnel, command);
            } catch (DispatchException e) {
                if (message.getMode() == MessageMode.REQUEST)
                    session.send(tunnel, MessageContent.toResponse(message.getHeader(), e.getResultCode()));
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
                if (event != null) {
                    try {
                        switch (event.getEventType()) {
                            case MESSAGE:
                                if (event instanceof MessageSendEvent)
                                    doOutputMessage(tunnel, (MessageSendEvent<UID>) event);
                                break;
                            case RESEND:
                                if (event instanceof MessageResendEvent)
                                    doOutputResend(tunnel, (MessageResendEvent<UID>) event);
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

    private void doOutputMessage(T tunnel, MessageSendEvent<UID> event) {
        try {
            if (tunnel.isClosed()) {
                throw new NetException("session is close");
            } else {
                NetSession<UID> session = tunnel.getSession();
                session.write(event);
            }
        } catch (Exception e) {
            LOGGER.error("doOutputMessage", e);
            event.sendFail(e);
        }
    }

    private void doOutputResend(T tunnel, MessageResendEvent<UID> event) {
        if (tunnel.isClosed()) {
            NetException exception = new NetException("session is close");
            event.resendFailed(exception);
        } else {
            NetSession<UID> session = tunnel.getSession();
            try {
                List<Message<UID>> resendMessages = session.getSentMessages(event.getResendRange());
                for (Message<UID> message : resendMessages) {
                    tunnel.write(message, null);
                }
                event.resendSuccess(tunnel, resendMessages);
            } catch (Throwable e) {
                event.resendFailed(e);
            }
        }
    }


}
