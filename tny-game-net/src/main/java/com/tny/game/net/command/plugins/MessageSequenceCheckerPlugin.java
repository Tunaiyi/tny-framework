package com.tny.game.net.command.plugins;

import com.tny.game.common.context.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.VoidControllerPlugin;
import com.tny.game.net.command.dispatcher.InvokeContext;
import com.tny.game.net.endpoint.Endpoint;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.Optional;

public class MessageSequenceCheckerPlugin implements VoidControllerPlugin<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CHECKER);

    private static AttrKey<Integer> CHECK_MESSAGE_ID = AttrKeys.key(MessageSequenceCheckerPlugin.class, "CHECK_MESSAGE_ID");

    @Override
    public void doExecute(Tunnel<Object> tunnel, Message<Object> message, InvokeContext context) {
        if (!tunnel.isLogin())
            return;
        Optional<Endpoint<Object>> endpointOpt = tunnel.getBindEndpoint();
        endpointOpt.ifPresent(endpoint -> {
            Integer lastHandledId = endpoint.attributes().getAttribute(CHECK_MESSAGE_ID, 0);
            MessageHeader header = message.getHeader();
            if (header.getId() > lastHandledId) {
                endpoint.attributes().setAttribute(CHECK_MESSAGE_ID, lastHandledId);
            } else {
                LOGGER.warn("message [{}] is handled, the id of the last message handled is {}",
                        message, lastHandledId);
                context.doneAndIntercept(NetResultCode.MESSAGE_HANDLED);
            }
        });
    }
}
