package com.tny.game.net.dispatcher.plugin;

import com.tny.game.net.base.Message;
import com.tny.game.net.dispatcher.CommandResult;

public interface ControllerPlugin<UID> {

    /**
     * 请求过滤
     *
     * @param message 消息
     * @param result  结果
     * @param context 上下文
     * @return 返回结果
     * @throws Exception 异常
     */
    CommandResult execute(Message<UID> message, CommandResult result, PluginContext context) throws Exception;

}