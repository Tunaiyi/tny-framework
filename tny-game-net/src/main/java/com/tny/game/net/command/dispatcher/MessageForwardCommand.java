package com.tny.game.net.command.dispatcher;

import com.tny.game.common.result.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
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

    private final RpcContext rpcContext;

    private final NetTunnel<RpcAccessIdentify> tunnel;

    public MessageForwardCommand(NetTunnel<RpcAccessIdentify> tunnel, Message message) {
        this.tunnel = tunnel;
        this.message = message;
        this.rpcContext = tunnel.getContext();
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
        RpcRemoteAccessPoint toPoint = rpcContext.getRpcForwarder()
                .forward(message, forwardHeader.getFrom(), forwardHeader.getSender(), forwardHeader.getTo(), forwardHeader.getReceiver());
        if (toPoint != null) {
            ForwardRpcServicer fromService = new ForwardRpcServicer(this.tunnel.getUserId());
            ForwardRpcServicer toService = toPoint.getForwardRpcServicer();
            toPoint.send(MessageContexts.copy(message)
                    .withHeader(RpcForwardHeaderBuilder.newBuilder()
                            .setFrom(fromService)
                            .setSender(forwardHeader.getSender())
                            .setTo(toService)
                            .setSender(forwardHeader.getReceiver())
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
