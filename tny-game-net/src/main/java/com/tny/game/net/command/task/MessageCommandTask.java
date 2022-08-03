package com.tny.game.net.command.task;

import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/14 2:33 下午
 */
public class MessageCommandTask implements CommandTask {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageCommandTask.class);

    private final Message message;

    private final NetTunnel<?> tunnel;

    public MessageCommandTask(NetTunnel<?> tunnel, Message message) {
        this.message = message;
        this.tunnel = tunnel;
    }

    private MessageForwardCommand tryForward(NetworkContext context) {
        NetBootstrapSetting setting = context.getSetting();
        if (!setting.isForwardable()) {
            return null;
        }
        RpcForwardHeader forwardHeader = this.message.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        if (forwardHeader == null) {
            return null;
        }
        ForwardPoint rpcServicer = forwardHeader.getTo();
        if (rpcServicer == null) {
            return null;
        }
        RpcServiceType currentType = setting.getRpcServiceType();
        NetAppContext appContext = context.getAppContext();
        if (rpcServicer.getServiceType() == currentType &&
                (!rpcServicer.isAccurately() || appContext != null && rpcServicer.getServerId() == appContext.getServerId())) {
            return null;
        }
        return new MessageForwardCommand(as(this.tunnel), this.message);
    }

    @Override
    public Command createCommand() {
        NetworkContext context = tunnel.getContext();
        MessageForwardCommand forwardCommand = tryForward(context);
        if (forwardCommand != null) {
            return forwardCommand;
        }
        switch (this.message.getMode()) {
            case PUSH:
            case REQUEST:
            case RESPONSE:
                MessageDispatcher dispatcher = context.getMessageDispatcher();
                return dispatcher.dispatch(this.tunnel, this.message);
            case PING:
                return new RunnableCommand(this.tunnel::pong);
            default:
        }
        return null;
    }

}
