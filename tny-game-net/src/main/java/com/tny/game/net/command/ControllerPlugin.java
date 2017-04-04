package com.tny.game.net.command;

import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.message.Message;

public interface ControllerPlugin<UID> {

    /**
     * 请求过滤
     *
     * @param tunnel  通道
     * @param message 消息
     * @param result  结果
     * @param context 上下文
     * @return 返回结果
     * @throws Exception 异常
     */
    CommandResult execute(Tunnel<UID> tunnel, Message<UID> message, CommandResult result, PluginContext context) throws Exception;

}