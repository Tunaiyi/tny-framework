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
    default void execute(Tunnel<UID> tunnel, Message<UID> message, CommandContext context, O attribute) throws Exception {
        if (context instanceof InvokeContext)
            this.execute(tunnel, message, (InvokeContext) context, attribute);
        else
            throw new CommandException(NetResultCode.DISPATCH_EXCEPTION,
                    StringAide.format("{} {} is not class{}", context.getClass(), context.getName(), InvokeContext.class));
    }


    /**
     * 请求过滤
     *
     * @param tunnel  通道
     * @param message 消息
     * @param context 上下文
     * @throws Exception 异常
     */
    void execute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context, O attribute) throws Exception;


}
