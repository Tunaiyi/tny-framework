package com.tny.game.net.command;

import com.tny.game.net.command.dispatcher.InvokeContext;
import com.tny.game.net.command.plugins.ControllerPlugin;
import com.tny.game.net.transport.Tunnel;
import com.tny.game.net.message.Message;

public interface VoidControllerPlugin<UID> extends ControllerPlugin<UID, Void> {

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
    default void execute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context, Void attribute) throws Exception {
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
    void doExecute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context) throws Exception;


}