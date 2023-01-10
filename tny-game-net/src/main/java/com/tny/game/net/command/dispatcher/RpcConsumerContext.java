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
package com.tny.game.net.command.dispatcher;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/19 17:22
 **/
public interface RpcConsumerContext extends RpcInvocationContext {

    static RpcConsumerContext create(Endpoint<?> endpoint, MessageContent content, RpcMonitor rpcMonitor) {
        return new RpcConsumerInvocationContext(endpoint, content, rpcMonitor);
    }

    boolean complete(Message message);

}
