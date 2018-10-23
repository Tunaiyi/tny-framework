package com.tny.game.net.command.plugins;

import com.tny.game.common.context.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.InvokeContext;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.Tunnel;
import org.slf4j.*;

public class MessageSequenceCheckerPlugin implements VoidControllerPlugin<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CHECKER);

    private SessionKeeperMannager sessionKeeperMannager;

    private static AttrKey<Integer> CHECK_MESSAGE_ID = AttrKeys.key(MessageSequenceCheckerPlugin.class, "CHECK_MESSAGE_ID");

    public MessageSequenceCheckerPlugin(SessionKeeperMannager sessionKeeperMannager) {
        this.sessionKeeperMannager = sessionKeeperMannager;
    }

    @Override
    public void doExecute(Tunnel<Object> tunnel, Message<Object> message, InvokeContext context) {
        if (!tunnel.isLogin())
            return;
        SessionKeeper<Object> keeper = sessionKeeperMannager.getKeeper(tunnel.getUserType());
        Session<Object> session = keeper.getSession(tunnel.getUserId());
        if (session != null) {
            Integer number = session.attributes().getAttribute(CHECK_MESSAGE_ID, 0);
            MessageHeader header = message.getHeader();
            if (header.getId() > number) {
                session.attributes().setAttribute(CHECK_MESSAGE_ID, number);
            } else {
                context.doneAndIntercept(NetResultCode.MESSAGE_HANDLED);
            }
        }
    }
}
