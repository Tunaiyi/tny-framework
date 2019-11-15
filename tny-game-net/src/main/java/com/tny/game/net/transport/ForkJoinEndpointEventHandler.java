package com.tny.game.net.transport;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.context.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.executor.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.event.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public class ForkJoinEndpointEventHandler<UID, E extends NetEndpoint<UID>> implements EndpointEventHandler<UID, E>, AppPrepareStart {

    public static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinEndpointEventHandler.class);
    private final static AttrKey<AtomicBoolean> HANDLE_INPUT_STATE = AttrKeys.key(ForkJoinEndpointEventHandler.class, "HANDLE_INPUT_STATE");
    private final static AttrKey<AtomicBoolean> HANDLE_OUTPUT_STATE = AttrKeys.key(ForkJoinEndpointEventHandler.class, "HANDLE_OUTPUT_STATE");

    private EndpointEventHandlerSetting setting;

    private ForkJoinPool forkJoinPool;

    private MessageHandler<UID> messageHandler;

    @FunctionalInterface
    public interface EndpointEventsBoxHandler<UID, E extends NetEndpoint<UID>> {

        void handler(EndpointEventsBox<UID> box, E endpoint, AtomicBoolean handling);

    }

    public ForkJoinEndpointEventHandler() {
    }

    public ForkJoinEndpointEventHandler(EndpointEventHandlerSetting setting) {
        this.setting = setting;
    }

    public ForkJoinEndpointEventHandler(EndpointEventHandlerSetting setting, ForkJoinPool forkJoinPool, MessageHandler<UID> messageHandler) {
        this.setting = setting;
        this.forkJoinPool = forkJoinPool;
        this.messageHandler = messageHandler;
    }

    private AtomicBoolean getInputHandleState(AttrKey<AtomicBoolean> key, Endpoint<UID> endpoint) {
        Attributes attributes = endpoint.attributes();
        AtomicBoolean state = attributes.getAttribute(key);
        if (state == null) {
            attributes.setAttributeIfNoKey(key, new AtomicBoolean(false));
        }
        return attributes.getAttribute(key);
    }


    @Override
    public void onInput(EndpointEventsBox<UID> box, E endpoint) {
        tryHandle(HANDLE_INPUT_STATE, endpoint, box, this::doInput);
    }

    @Override
    public void onOutput(EndpointEventsBox<UID> box, E endpoint) {
        tryHandle(HANDLE_OUTPUT_STATE, endpoint, box, this::doOutput);
    }

    private void tryHandle(AttrKey<AtomicBoolean> key, E endpoint, EndpointEventsBox<UID> box, EndpointEventsBoxHandler<UID, E> consumer) {
        AtomicBoolean handling = getInputHandleState(key, endpoint);
        if (handling.get())
            return;
        if (handling.compareAndSet(false, true)) {
            forkJoinPool.submit(() -> consumer.handler(box, endpoint, handling));
        }
    }

    private void doInput(EndpointEventsBox<UID> box, E endpoint, AtomicBoolean handled) {
        try {
            while (box.hasInputEvent()) {
                EndPointInputEvent<UID> event = box.pollInputEvent();
                if (event == null)
                    continue;
                doInputEvent(event);
            }
        } finally {
            handled.set(false);
            if (box.hasInputEvent())
                this.onInput(box, endpoint);
        }
    }

    private void doInputEvent(EndPointInputEvent<UID> event) {
        try {
            switch (event.getEventType()) {
                case RECEIVE: {
                    doEndpointReceiveEvent(as(event));
                    break;
                }
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    private void doEndpointReceiveEvent(EndpointReceiveEvent<UID> event) {
        Optional<NetTunnel<UID>> tunnelOpt = event.getTunnel();
        if (tunnelOpt.isPresent()) {
            NetTunnel<UID> tunnel = tunnelOpt.get();
            messageHandler.handle(tunnel, event.getMessage(), event.getRespondFuture());
        }
    }


    private void doOutput(EndpointEventsBox<UID> box, E endpoint, AtomicBoolean handled) {
        try {
            while (box.hasOutputEvent()) {
                EndPointOutputEvent<UID> event = box.pollOutputEvent();
                if (event == null)
                    continue;
                doOutputEvent(endpoint, box, event);
            }
        } finally {
            handled.set(false);
            if (box.hasOutputEvent())
                this.onOutput(box, endpoint);
        }
    }

    private void doOutputEvent(E endpoint, EndpointEventsBox<UID> box, EndPointOutputEvent<UID> event) {
        try {
            switch (event.getEventType()) {
                case SEND:
                    if (event instanceof EndpointSendEvent) {
                        doEndpointSendEvent(endpoint, as(event));
                    }
                    break;
                case RESEND:
                    if (event instanceof EndpointResendEvent) {
                        doEndpointResendEvent(endpoint, as(event));
                    }
                    break;
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    // private void doEndpointResendEvent(E endpoint, Object as) {
    // }

    private void doEndpointSendEvent(E endpoint, EndpointSendEvent<UID> sendEvent) {
        if (endpoint.isClosed()) {
            NetException exception = new NetException("endpoint is close");
            sendEvent.fail(exception);
        } else {
            Optional<NetTunnel<UID>> tunnelOpt = sendEvent.getTunnel();
            if (tunnelOpt.isPresent()) {
                NetTunnel<UID> tunnel = tunnelOpt.get();
                // box.addSentMessage(message);
                try {
                    endpoint.writeMessage(tunnel, sendEvent.getContext());
                } catch (Throwable e) {
                    sendEvent.fail(e);
                }
            } else {
                NetException exception = new NetException("tunnel is close");
                sendEvent.fail(exception);
            }
        }
    }

    private void doEndpointResendEvent(E endpoint, EndpointResendEvent<UID> resendEvent) {
        if (endpoint.isClosed())
            return;
        NetTunnel<UID> tunnel = resendEvent.getTunnel().orElse(null);
        if (tunnel != null && tunnel.isAlive()) {
            List<Message<UID>> messages = endpoint.getSendMessages(resendEvent.getFilter());
            for (Message<UID> message : messages) {
                try {
                    tunnel.write(message, null);
                } catch (Throwable e) {
                    LOGGER.error("resent message {} exception", message, e);
                }
            }
        }
    }


    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_7);
    }

    @Override
    public void prepareStart() {
        this.forkJoinPool = ForkJoinPools.pool(this.setting.getThreads(), this.getClass().getSimpleName(), true);
        MessageDispatcher messageDispatcher = UnitLoader.getLoader(MessageDispatcher.class).getUnitAnCheck(setting.getMessageDispatcher());
        MessageCommandExecutor commandExecutor = UnitLoader.getLoader(MessageCommandExecutor.class).getUnitAnCheck(setting.getCommandExecutor());
        this.messageHandler = new DefaultMessageHandler<>(messageDispatcher, commandExecutor);
    }
}
