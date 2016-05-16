package com.tny.game.net.dispatcher.plugin;

import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.dispatcher.Request;

public interface ControllerPlugin {

    /**
     * 请求过滤
     *
     * @param request 请求对象
     * @param result  结果
     * @param context 上下文
     * @return 返回结果
     * @throws Exception
     */
    CommandResult execute(Request request, CommandResult result, PluginContext context) throws Exception;

}
