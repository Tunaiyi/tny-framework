package com.tny.game.net.command;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.UnitLoader;
import com.tny.game.common.worker.command.Command;
import com.tny.game.net.base.NetResultCode;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.command.executor.DispatchCommandExecutor;
import com.tny.game.net.exception.CommandException;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import static com.tny.game.common.lifecycle.LifecycleLevel.SYSTEM_LEVEL_10;
import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public class DispatchMessageHandler<UID> implements MessageHandler<UID>, AppPrepareStart {

    public static final Logger LOGGER = LoggerFactory.getLogger(DispatchMessageHandler.class);

    private DispatchMessageHandlerSetting setting;

    private MessageDispatcher messageDispatcher;

    private DispatchCommandExecutor commandExecutor;

    public DispatchMessageHandler(DispatchMessageHandlerSetting setting) {
        this.setting = setting;
    }

    @Override
    public void handle(NetTunnel<UID> tunnel, NetMessage<UID> message) {
        MessageMode mode = message.getMode();
        if (tunnel == null)
            return;
        switch (mode) {
            case PUSH:
            case REQUEST:
            case RESPONSE:
                try {
                    if (tunnel.receive(message)) {
                        Command command = messageDispatcher.dispatch(tunnel, message);
                        if (command != null)
                            commandExecutor.submit(tunnel, command);
                    }
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
        switch (message.getMode()) {
            case REQUEST:
                if (e instanceof CommandException) {
                    CommandException ce = as(e);
                    TunnelsUtils.responseMessage(tunnel, message, ce.getResultCode(), ce.getBody());
                } else {
                    TunnelsUtils.responseMessage(tunnel, message, NetResultCode.RECEIVE_ERROR, null);
                }
                break;
            default:
                break;

        }
        LOGGER.error("#DefaultMessageHandler#处理 {} 异常", message, e);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() {
        this.messageDispatcher = UnitLoader.getLoader(MessageDispatcher.class).getUnitAnCheck(setting.getMessageDispatcher());
        this.commandExecutor = UnitLoader.getLoader(DispatchCommandExecutor.class).getUnitAnCheck(setting.getDispatchCommandExecutor());
    }
}
