package com.tny.game.net.plugin;

import com.tny.game.net.command.CommandResult;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.Session;

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
    CommandResult execute(Session<UID> session, Message<UID> message, CommandResult result, PluginContext context) throws Exception;

}