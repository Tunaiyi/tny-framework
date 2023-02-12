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

import com.tny.game.common.worker.command.*;
import org.slf4j.*;

import static com.tny.game.net.command.dispatcher.RpcTransactionContext.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/3 03:42
 **/
public class RpcNoopCommand extends BaseCommand implements RpcCommand {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcNoopCommand.class);

    private final RpcEnterContext rpcContext;

    public RpcNoopCommand(RpcEnterContext rpcContext) {
        this.rpcContext = rpcContext;
    }

    @Override
    protected void action() throws Throwable {
        RpcContexts.setCurrent(rpcContext);
        try {
            var message = rpcContext.netMessage();
            rpcContext.invoke(returnOperation(message));
            rpcContext.completeSilently();
        } catch (Throwable cause) {
            LOGGER.error("", cause);
            rpcContext.complete(cause);
        } finally {
            RpcContexts.clear();
        }
    }

}
