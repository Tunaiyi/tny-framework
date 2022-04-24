package com.tny.game.net.command.plugins;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

public interface VoidInvokeCommandPlugin<UID> extends CommandPlugin<UID, Void> {

    @Override
    default Class<Void> getAttributesClass() {
        return Void.class;
    }

    /**
     * 请求过滤
     *
     * @param tunnel  通道
     * @param message 消息
     * @param context 上下文
     * @throws Exception 异常
     */
    @Override
    default void execute(Tunnel<UID> tunnel, Message message, MessageCommandContext context, Void attribute) throws Exception {
        this.doExecute(tunnel, message, context);
    }

    /**
     * 请求过滤
     *
     * @param tunnel  通道
     * @param message 消息
     * @param context 上下文
     * @throws Exception 异常
     */
    void doExecute(Tunnel<UID> tunnel, Message message, MessageCommandContext context) throws Exception;

}