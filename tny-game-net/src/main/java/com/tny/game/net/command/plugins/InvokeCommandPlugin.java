package com.tny.game.net.command.plugins;

import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
public interface InvokeCommandPlugin<UID, O> extends CommandPlugin<UID, O> {

    /**
     * 请求过滤
     *
     * @param tunnel  通道
     * @param message 消息
     * @param context 上下文
     * @throws Exception 异常
     */
    @Override
    default void execute(Tunnel<UID> tunnel, Message message, MessageCommandContext context, O attribute) throws Exception {
        if (context instanceof ControllerMessageCommandContext) {
            this.execute(tunnel, message, (ControllerMessageCommandContext)context, attribute);
        } else {
            throw new CommandException(NetResultCode.DISPATCH_EXCEPTION,
                    StringAide.format("{} {} is not class{}", context.getClass(), context.getName(), ControllerMessageCommandContext.class));
        }
    }

    /**
     * 请求过滤
     *
     * @param tunnel  通道
     * @param message 消息
     * @param context 上下文
     * @throws Exception 异常
     */
    void execute(Tunnel<UID> tunnel, Message message, ControllerMessageCommandContext context, O attribute) throws Exception;

}
