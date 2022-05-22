package com.tny.game.net.endpoint;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;

/**
 * 终端上下文
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/17 7:32 下午
 */
public interface EndpointContext {

    /**
     * @return 消息分发器
     */
    MessageDispatcher getMessageDispatcher();

    /**
     * @return 命令任务执行器
     */
    CommandTaskBoxProcessor getCommandTaskProcessor();

}
