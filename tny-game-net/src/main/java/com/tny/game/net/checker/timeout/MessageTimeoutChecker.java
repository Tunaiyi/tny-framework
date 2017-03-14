package com.tny.game.net.checker.timeout;

import com.tny.game.common.result.ResultCode;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.Message;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.dispatcher.ControllerHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kun Yang on 2017/3/4.
 */
public class MessageTimeoutChecker implements ControllerChecker<Long> {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(CoreLogger.DISPATCHER);

    @Override
    public ResultCode check(Message message, ControllerHolder holder, AppContext context, Long attribute) {
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