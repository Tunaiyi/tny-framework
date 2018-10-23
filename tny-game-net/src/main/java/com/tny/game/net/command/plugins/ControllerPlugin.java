package com.tny.game.net.command.plugins;

import com.tny.game.net.command.dispatcher.InvokeContext;
import com.tny.game.net.transport.Tunnel;
import com.tny.game.net.message.Message;

public interface ControllerPlugin<UID, O> {

    /**
     * @return 获取参数类型
     */
    Class<O> getAttributesClass();

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