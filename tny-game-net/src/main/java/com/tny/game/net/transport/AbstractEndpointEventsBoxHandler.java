package com.tny.game.net.transport;

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
import com.tny.game.net.transport.listener.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public abstract class AbstractEndpointEventsBoxHandler<UID, E extends NetEndpoint<UID>> implements EndpointEventsBoxHandler<UID, E>, AppPrepareStart {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractEndpointEventsBoxHandler.class);

    private final static AttrKey<AtomicBoolean> HANDLE_INPUT_STATE = AttrKeys.key(AbstractEndpointEventsBoxHandler.class, "HANDLE_INPUT_STATE");
    private final static AttrKey<AtomicBoolean> HANDLE_OUTPUT_STATE = AttrKeys.key(AbstractEndpointEventsBoxHandler.class, "HANDLE_OUTPUT_STATE");

    protected EndpointEventHandlerSetting setting;

    private MessageHandler<UID> messageHandler;

    private final EndpointEventsBoxProcessor<UID, E> inputHandler = this::doInput;
    private final EndpointEventsBoxProcessor<UID, E> outputHandler = this::doOutput;

    public AbstractEndpointEventsBoxHandler() {
    }

    public AbstractEndpointEventsBoxHandler(EndpointEventHandlerSetting setting) {
        this.setting = setting;
    }

    public AbstractEndpointEventsBoxHandler(EndpointEventHandlerSetting setting, MessageHandler<UID> messageHandler) {
        this.setting = setting;
        this.messageHandler = messageHandler;
    }

    private AtomicBoolean getHandleState(AttrKey<AtomicBoolean> key, Endpoint<UID> endpoint) {
        Attributes attributes = endpoint.attributes();
        AtomicBoolean state = attributes.getAttribute(key);
        if (state == null) {
            attributes.setAttributeIfNoKey(key, new AtomicBoolean(false));
        }
        return attributes.getAttribute(key);
    }

    @Override
    public void onInput(EndpointEventsBox<UID> box, E endpoint) {
        if (box.hasInputEvent()) {
            tryHandle(EndpointEventFlow.INPUT, HANDLE_INPUT_STATE, endpoint, box, this.inputHandler);
        }
    }

    @Override
    public void onOutput(EndpointEventsBox<UID> box, E endpoint) {
        if (box.hasOutputEvent()) {
            tryHandle(EndpointEventFlow.OUTPUT, HANDLE_OUTPUT_STATE, endpoint, box, this.outputHandler);
        }
    }

    private void tryHandle(EndpointEventFlow flow, AttrKey<AtomicBoolean> key, E endpoint, EndpointEventsBox<UID> box,
            EndpointEventsBoxProcessor<UID, E> consumer) {
        // 查看当前 endpoint 是否已经提交到处理线程
        AtomicBoolean handling = getHandleState(key, endpoint);
        if (handling.get()) {
            return;
        }
        if (handling.compareAndSet(false, true)) {
            // 如果未处理则提交线程处理
            execute(flow, () -> consumer.handler(box, endpoint, handling));
        }
    }

    protected abstract void execute(EndpointEventFlow flow, Runnable handle);

    private void doInput(EndpointEventsBox<UID> box, E endpoint, AtomicBoolean handled) {
        try {
            while (box.hasInputEvent()) {
                EndpointInputEvent<UID> event = box.pollInputEvent();
                if (event == null) {
                    continue;
                }
                doInputEvent(event);
            }
        } finally {
            handled.set(false);
            this.onInput(box, endpoint);
        }
    }

    @Override
    public void onInputEvent(E endpoint, EndpointInputEvent<UID> event) {
        this.doInputEvent(event);
    }

    private void doInputEvent(EndpointInputEvent<UID> event) {
        if (event.getEventType() == EndpointEventType.RECEIVE) {
            doEndpointReceiveEvent(as(event));
        }
    }

    private void doEndpointReceiveEvent(EndpointReceiveEvent<UID> event) {
        Optional<NetTunnel<UID>> tunnelOpt = event.getTunnel();
        if (tunnelOpt.isPresent()) {
            NetTunnel<UID> tunnel = tunnelOpt.get();
            this.messageHandler.handle(tunnel, event.getMessage(), event.getRespondFuture());
        }
    }

    private void doOutput(EndpointEventsBox<UID> box, E endpoint, AtomicBoolean handled) {
        try {
            while (box.hasOutputEvent()) {
                EndpointOutputEvent<UID> event = box.pollOutputEvent();
                if (event == null) {
                    continue;
                }
                doOutputEvent(endpoint, event);
            }
        } finally {
            handled.set(false);
            this.onOutput(box, endpoint);
        }
    }

    @Override
    public void onOutputEvent(E endpoint, EndpointOutputEvent<UID> event) {
        this.doOutputEvent(endpoint, event);
    }

    private void doOutputEvent(E endpoint, EndpointOutputEvent<UID> event) {
        try {
            switch (event.getEventType()) {
                case SEND:
                    if (event instanceof EndpointSendEvent) {
                        // TODO 通过 channel 线程处理
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

    private void doEndpointSendEvent(E endpoint, EndpointSendEvent<UID> sendEvent) {
        if (endpoint.isClosed()) {
            NetException exception = new NetException("endpoint is close");
            sendEvent.fail(exception);
        } else {
            Optional<NetTunnel<UID>> tunnelOpt = sendEvent.getTunnel();
            if (tunnelOpt.isPresent()) {
                NetTunnel<UID> tunnel = tunnelOpt.get();
                try {
                    //                    tunnel.write(endpoint, sendEvent.getContext());
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
        if (endpoint.isClosed()) {
            return;
        }
        NetTunnel<UID> tunnel = resendEvent.getTunnel().orElse(null);
        if (tunnel != null && tunnel.isActive()) {
            List<Message> messages = endpoint.getSentMessages(resendEvent.getFilter());
            for (Message message : messages) {
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
        MessageDispatcher messageDispatcher = UnitLoader.getLoader(MessageDispatcher.class).getUnitAnCheck(this.setting.getMessageDispatcher());
        MessageCommandExecutor commandExecutor = UnitLoader.getLoader(MessageCommandExecutor.class).getUnitAnCheck(this.setting.getCommandExecutor());
        this.messageHandler = new DefaultMessageHandler<>(messageDispatcher, commandExecutor);
    }

}
