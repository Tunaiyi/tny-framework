package com.tny.game.net.command.plugins;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

@UnitInterface
public interface CommandPlugin<UID, O> {

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
    void execute(Tunnel<UID> tunnel, Message message, MessageCommandContext context, O attribute) throws Exception;

}