package com.tny.game.net.command.plugins;

import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.InvokeContext;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.Tunnel;
import org.slf4j.*;

/**
 * 检查消息超时Plugin
 * 参数 为超时时间ms:
 *
 * @3000 3秒
 * <p>
 * Created by Kun Yang on 2017/3/4.
 */
public class MessageTimeoutCheckerPlugin<UID> implements ControllerPlugin<UID, Long> {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);


    @Override
    public Class<Long> getAttributesClass() {
        return Long.class;
    }

    @Override
    public void execute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context, Long attribute) throws Exception {
        if (attribute <= 0)
            return;
        // 是否需要做超时判断
        MessageHeader header = message.getHeader();
        if (System.currentTimeMillis() + attribute > header.getTime()) {
            DISPATCHER_LOG.warn("调用 {} 业务方法失败, 消息超时!", context.getName());
            context.doneAndIntercept(NetResultCode.REQUEST_TIMEOUT);
        }
    }
}
