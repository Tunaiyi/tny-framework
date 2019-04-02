package com.tny.game.net.command.plugins;

import com.tny.game.common.context.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

public class MessageSequenceCheckerPlugin implements VoidCommandPlugin<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CHECKER);

    private static AttrKey<Integer> CHECK_MESSAGE_ID = AttrKeys.key(MessageSequenceCheckerPlugin.class, "CHECK_MESSAGE_ID");

    @Override
    public void doExecute(Tunnel<Object> tunnel, Message<Object> message, CommandContext context) {
        if (!tunnel.isLogin())
            return;
        Endpoint<Object> endpoint = tunnel.getEndpoint();
        Integer lastHandledId = endpoint.attributes().getAttribute(CHECK_MESSAGE_ID, 0);
        MessageHead head = message.getHead();
        if (head.getId() > lastHandledId) {
            endpoint.attributes().setAttribute(CHECK_MESSAGE_ID, lastHandledId);
        } else {
            LOGGER.warn("message [{}] is handled, the id of the last message handled is {}", message, lastHandledId);
            context.doneAndIntercept(NetResultCode.MESSAGE_HANDLED);
        }
    }
}
