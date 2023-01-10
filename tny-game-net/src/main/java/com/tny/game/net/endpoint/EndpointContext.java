/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.endpoint;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.rpc.*;

/**
 * 终端上下文
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/17 7:32 下午
 */
public interface EndpointContext {

    /**
     * @return 接入模式
     */
    NetAccessMode getAccessMode();

    /**
     * @return 消息分发器
     */
    MessageDispatcher getMessageDispatcher();

    /**
     * @return 命令任务执行器
     */
    CommandBoxProcessor getCommandTaskProcessor();

}
