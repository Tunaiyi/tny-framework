package com.tny.game.net.common.handle;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrUtils;
import com.tny.game.net.command.MessageCommand;
import com.tny.game.net.command.MessageCommandExecutor;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.command.MessageDispatcher;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.SessionInputEventHandler;
import com.tny.game.net.session.event.SessionInputEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public class DispatchMessageInputEvetHandler<UID, S extends NetSession<UID>> implements SessionInputEventHandler<UID, S> {

    public static final Logger LOGGER = LoggerFactory.getLogger(DispatchMessageInputEvetHandler.class);

    private MessageDispatcher messageDispatcher;

    private MessageCommandExecutor commandExecutor;

    private final static AttrKey<AtomicBoolean> INPUT_HANDLE_STATE = AttrUtils.key(DispatchMessageInputEvetHandler.class, "INPUT_HANDLE_STATE");

    public DispatchMessageInputEvetHandler(MessageDispatcher messageDispatcher, MessageCommandExecutor commandExecutor) {
        this.messageDispatcher = messageDispatcher;
        this.commandExecutor = commandExecutor;
    }

    private AtomicBoolean getInputHandleState(S session) {
        AtomicBoolean state = session.attributes().getAttribute(INPUT_HANDLE_STATE);
        if (state == null) {
            synchronized (session) {
                state = session.attributes().getAttribute(INPUT_HANDLE_STATE);
                if (state != null)
                    return state;
                state = new AtomicBoolean(false);
                session.attributes().setAttribute(INPUT_HANDLE_STATE, state);
            }
        }
        return state;
    }

    @Override
    public void onInput(S session) {
        while (session.hasInputEvent()) {
            SessionInputEvent event = session.pollInputEvent();
            if (event != null) {
                Message<?> message = event.getMessage();
                try {
                    switch (event.getEventType()) {
                        case MESSAGE:
                            MessageCommand<CommandResult> command = messageDispatcher.dispatch(message, session);
                            commandExecutor.submit(session, command);
                        case PING:
                    }
                } catch (DispatchException e) {
                    if (message.getMode() == MessageMode.REQUEST)
                        session.sendMessage(message, MessageContent.toResponse(e.getResultCode(), message.getToMessage()));
                    LOGGER.error("#ThreadPoolSessionInputEvetHandler#处理InputEvent异常", e);
                }
            } else {
                continue;
            }
        }
    }

}
