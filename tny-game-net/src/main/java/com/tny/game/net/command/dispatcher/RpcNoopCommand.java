/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.concurrent.worker.*;
import org.slf4j.*;

import java.util.concurrent.CompletableFuture;

import static com.tny.game.net.command.dispatcher.RpcTransactionContext.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/3 03:42
 **/
public class RpcNoopCommand implements RpcCommand {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcNoopCommand.class);

    private final RpcEnterContext rpcContext;

    public RpcNoopCommand(RpcEnterContext rpcContext) {
        this.rpcContext = rpcContext;
    }

    @Override
    public CompletableFuture<Object> execute(AsyncWorker worker) {
        RpcContexts.setCurrent(rpcContext);
        try {
            var message = rpcContext.getMessage();
            rpcContext.invoke(returnOperation(message));
            rpcContext.completeSilently();
        } catch (Throwable cause) {
            LOGGER.error("", cause);
            rpcContext.complete(cause);
        } finally {
            RpcContexts.clear();
        }
        return null;
    }

}
