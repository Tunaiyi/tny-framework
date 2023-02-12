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

import com.tny.game.common.result.*;
import com.tny.game.common.worker.command.*;
import org.slf4j.*;

import static com.tny.game.net.command.dispatcher.RpcTransactionContext.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/3 03:42
 **/
public class RpcRespondCommand extends BaseCommand implements RpcCommand {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcRespondCommand.class);

    private final RpcEnterContext rpcContext;

    private final ResultCode code;

    private final Object body;

    public RpcRespondCommand(RpcEnterContext rpcContext, ResultCode code, Object body) {
        this.rpcContext = rpcContext;
        this.code = code;
        this.body = body;
    }

    @Override
    protected void action() throws Throwable {
        RpcContexts.setCurrent(rpcContext);
        try {
            var message = rpcContext.netMessage();
            rpcContext.invoke(errorOperation(message));
            rpcContext.complete(code, body);
        } catch (Throwable cause) {
            rpcContext.complete(cause);
        } finally {
            RpcContexts.clear();
        }
    }

}
