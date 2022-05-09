package com.tny.game.net.command.task;

import com.tny.game.common.worker.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/14 2:33 下午
 */
public class MessageCommandTask implements CommandTask {

    private final Message message;

    private final NetTunnel<?> tunnel;

    private final EndpointContext endpointContext;

    public MessageCommandTask(NetTunnel<?> tunnel, Message message, EndpointContext endpointContext) {
        this.message = message;
        this.endpointContext = endpointContext;
        this.tunnel = tunnel;
    }

    @Override
    public Command createCommand() {
        if (this.message.existHeader(MessageHeaderConstants.RPC_FORWARD_HEADER)) {
            return new MessageForwardCommand(as(this.tunnel), this.message);
        }
        switch (this.message.getMode()) {
            case PUSH:
            case REQUEST:
            case RESPONSE:
                return this.endpointContext.getMessageDispatcher().dispatch(this.tunnel, this.message);
            case PING:
                return new RunnableCommand(this.tunnel::pong);
            default:
        }
        return null;
    }

}
