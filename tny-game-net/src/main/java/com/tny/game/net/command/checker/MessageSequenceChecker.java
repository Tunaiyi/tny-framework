package com.tny.game.net.command.checker;

import com.tny.game.common.context.*;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.ControllerHolder;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.*;
import org.slf4j.*;

@Deprecated
public class MessageSequenceChecker implements ControllerChecker<Object, Object> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CHECKER);

    protected static AttrKey<Integer> CHECK_MESSAGE_ID = AttrKeys.key(MessageSequenceChecker.class, "CHECK_MESSAGE_ID");

    public MessageSequenceChecker() {
    }

    @Override
    public ResultCode check(Tunnel<Object> tunnel, Message<Object> message, ControllerHolder holder, Object attribute) {
        // Session session = tunnel.getSession();
        // Integer number = session.attributes().getAttribute(CHECK_MESSAGE_ID, 0);
        // MessageHeader header = message.getHeader();
        // if (header.getId() > number) {
        //     session.attributes().setAttribute(CHECK_MESSAGE_ID, number);
        //     return ResultCode.SUCCESS;
        // }
        return NetResultCode.MESSAGE_HANDLE;
    }
}
