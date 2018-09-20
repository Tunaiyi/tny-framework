package com.tny.game.net.command;

import com.tny.game.net.transport.message.Message;
import com.tny.game.net.transport.Tunnel;

public interface ControllerPlugin<UID> {

    /**
     * 请求过滤
     *
     * @param tunnel  通道
     * @param message 消息
     * @param context 上下文
     * @throws Exception 异常
     */
    void execute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context) throws Exception;

}