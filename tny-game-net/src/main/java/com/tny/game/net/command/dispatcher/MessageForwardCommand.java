package com.tny.game.net.command.dispatcher;

import com.tny.game.common.result.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/3 03:42
 **/
public class MessageForwardCommand extends BaseCommand {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageForwardCommand.class);

    private final Message message;

    private final NetworkContext context;

    private final NetTunnel<RpcAccessIdentify> tunnel;

    public MessageForwardCommand(NetTunnel<RpcAccessIdentify> tunnel, Message message) {
        this.tunnel = tunnel;
        this.message = message;
        this.context = tunnel.getContext();
    }

    @Override
    protected void action() throws Throwable {
        Throwable exception = null;
        for (int time = 0; time < 5; time++) {
            try {
                forward();
                return;
            } catch (Throwable e) {
                LOGGER.error("forward exception", e);
                exception = e;
            }
        }
        throw exception;
    }

    private void forward() {
        RpcForwardHeader forwardHeader = message.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        RpcForwardAccess toAccess = context.getRpcForwarder().forward(message, forwardHeader);
        if (toAccess != null && toAccess.isActive()) {
            ForwardPoint fromPoint = new ForwardPoint(this.tunnel.getUserId());
            RpcServicerPoint toPoint = toAccess.getForwardPoint();
            toAccess.send(MessageContexts.copy(message)
                    .withHeader(RpcForwardHeaderBuilder.newBuilder()
                            .setFrom(fromPoint)
                            .setSender(forwardHeader.getSender())
                            .setTo(toPoint)
                            .setReceiver(forwardHeader.getReceiver())
                            .build())
                    .withHeader(RpcOriginalMessageIdHeaderBuilder.newBuilder()
                            .setMessageId(message.getId())
                            .build()));
        } else {
            if (message.getMode() == MessageMode.REQUEST) {
                ResultCode code = NetResultCode.RPC_SERVICE_NOT_AVAILABLE;
                TunnelAide.responseMessage(this.tunnel, code, MessageContexts.respond(this.message, code, null, message.getId()));
            }
        }
    }

}
