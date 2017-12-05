package com.tny.game.net.command.checker;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrKeys;
import com.tny.game.common.result.ResultCode;
import com.tny.game.suite.app.CoreResponseCode;
import com.tny.game.suite.app.NetLogger;
import com.tny.game.net.command.dispatcher.ControllerHolder;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.Session;
import com.tny.game.net.tunnel.Tunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSequenceChecker implements ControllerChecker<Object, Object> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CHECKER);

    protected static AttrKey<Integer> CHECK_MESSAGE_ID = AttrKeys.key(MessageSequenceChecker.class, "CHECK_MESSAGE_ID");

    public MessageSequenceChecker() {
    }

    @Override
    public ResultCode check(Tunnel<Object> tunnel, Message<Object> message, ControllerHolder holder, Object attribute) {
        Session session = tunnel.getSession();
        Integer number = session.attributes().getAttribute(CHECK_MESSAGE_ID, 0);
        if (message.getID() > number) {
            session.attributes().setAttribute(CHECK_MESSAGE_ID, number);
            return ResultCode.SUCCESS;
        }
        return CoreResponseCode.MESSAGE_HANDLE;
    }
}
