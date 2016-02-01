package com.tny.game.net.dispatcher.plugin;

import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.dispatcher.Request;

public interface ControllerPlugin {

    /**
     * 请求过滤
     *
     * @param session  该用户的会话对象
     * @param request  请求对象
     * @param response 相应对象
     * @return 返回true執行下一個, 返回false跳出迭代
     */
    public CommandResult execute(Request request, CommandResult result, PluginContext context) throws Exception;

}
