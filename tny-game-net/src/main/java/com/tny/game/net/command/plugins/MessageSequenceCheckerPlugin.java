package com.tny.game.net.command.plugins;

import com.tny.game.common.context.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.transport.Tunnel;
import com.tny.game.net.transport.message.*;
import org.slf4j.*;

public class MessageSequenceCheckerPlugin implements VoidControllerPlugin<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CHECKER);

    private static AttrKey<Integer> CHECK_MESSAGE_ID = AttrKeys.key(MessageSequenceCheckerPlugin.class, "CHECK_MESSAGE_ID");

    public MessageSequenceCheckerPlugin() {
    }

    @Override
    public void doExecute(Tunnel<Object> tunnel, Message<Object> message, InvokeContext context) {
        tunnel.getBindSession().ifPresent(session -> {
            Integer number = session.attributes().getAttribute(CHECK_MESSAGE_ID, 0);
            MessageHeader header = message.getHeader();
            if (header.getId() > number) {
                session.attributes().setAttribute(CHECK_MESSAGE_ID, number);
            } else {
                context.doneAndIntercept(NetResultCode.MESSAGE_HANDLED);
            }
        });
    }
}
