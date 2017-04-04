package com.tny.game.net.common.checker;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.common.dispatcher.ControllerHolder;
import com.tny.game.net.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (System.currentTimeMillis() + attribute > message.getTime()) {
            DISPATCHER_LOG.error("消息 {}.{} 业务方法超时", holder.getControllerClass(), holder.getName());
            return CoreResponseCode.REQUEST_TIMEOUT;
        }
        return ResultCode.SUCCESS;
    }

    @Override
    public Class<?> getAttributesClass() {
        return Long.class;
    }
}
