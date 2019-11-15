package com.tny.game.net.command;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.executor.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/3/18.
 */
@Unit
public class DefaultMessageHandler<UID> implements MessageHandler<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageHandler.class);

    private MessageDispatcher messageDispatcher;

    private MessageCommandExecutor commandExecutor;

    public DefaultMessageHandler() {
    }

    public DefaultMessageHandler(MessageDispatcher messageDispatcher, MessageCommandExecutor commandExecutor) {
        this.messageDispatcher = messageDispatcher;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void handle(NetTunnel<UID> tunnel, Message<UID> message, RespondFuture<UID> future) {
        MessageMode mode = message.getMode();
        if (tunnel == null)
            return;
        switch (mode) {
            case PUSH:
            case REQUEST:
            case RESPONSE:
                try {
                    Command command = messageDispatcher.dispatch(tunnel, message);
                    if (command != null)
                        commandExecutor.submit(tunnel, command);
                    if (future != null)
                        commandExecutor.submit(tunnel, new RespondFutureCommand<>(message, future));
                } catch (Throwable e) {
                    handleException(tunnel, message, e);
                }
                return;
            case PING:
                tunnel.pong();
                return;
            default:
        }
    }

    private void handleException(NetTunnel<UID> tunnel, Message<UID> message, Throwable e) {
        if (message.getMode() == MessageMode.REQUEST) {
            if (e instanceof CommandException) {
                CommandException ce = as(e);
                TunnelAides.responseMessage(tunnel, message, ce.getResultCode(), ce.getBody());
            } else {
                TunnelAides.responseMessage(tunnel, message, NetResultCode.RECEIVE_ERROR, null);
            }
        }
        LOGGER.error("#DefaultMessageHandler#处理 {} 异常", message, e);
    }

}
