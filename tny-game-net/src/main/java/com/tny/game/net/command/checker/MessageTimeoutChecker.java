package com.tny.game.net.command.checker;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.ControllerHolder;
import com.tny.game.net.transport.Tunnel;
import com.tny.game.net.transport.message.*;
import org.slf4j.*;

/**
 * Created by Kun Yang on 2017/3/4.
 */
public class MessageTimeoutChecker<UID> implements ControllerChecker<UID, Long> {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    @Override
    public ResultCode check(Tunnel<UID> tunnel, Message<UID> message, ControllerHolder holder, Long attribute) {
        if (attribute <= 0)
            return ResultCode.SUCCESS;

        if (DISPATCHER_LOG.isDebugEnabled())
            DISPATCHER_LOG.debug("检测消息 {}.{} 业务方法超时", holder.getControllerClass(), holder.getName());
        // 是否需要做超时判断
        MessageHeader header = message.getHeader();
        if (System.currentTimeMillis() + attribute > header.getTime()) {
            DISPATCHER_LOG.error("消息 {}.{} 业务方法超时", holder.getControllerClass(), holder.getName());
            return NetResultCode.REQUEST_TIMEOUT;
        }
        return ResultCode.SUCCESS;
    }

    @Override
    public Class<?> getAttributesClass() {
        return Long.class;
    }
}
