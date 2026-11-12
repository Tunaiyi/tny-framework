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
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.concurrent.CompletableFuture;

import static com.tny.game.net.command.dispatcher.RpcTransactionContext.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/3 03:42
 **/
public class RpcForwardCommand implements RpcCommand {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcForwardCommand.class);

    private final RpcEnterContext rpcContext;

    public RpcForwardCommand(RpcEnterContext rpcContext) {
        this.rpcContext = rpcContext;
    }

    @Override
    public CompletableFuture<Object> execute(AsyncWorker worker) throws Throwable {
        Throwable exception = null;
        for (int time = 0; time < 5; time++) {
            try {
                RpcContexts.setCurrent(rpcContext);
                forward();
                return null;
            } catch (Throwable cause) {
                LOGGER.error("forward exception", cause);
                exception = cause;
                rpcContext.complete(cause);
            } finally {
                RpcContexts.clear();
            }
        }
        throw exception;
    }

    private void forward() {
        var tunnel = rpcContext.<RpcServicer>netTunnel();
        var message = rpcContext.getMessage();
        var networkContext = rpcContext.networkContext();
        RpcForwardHeader forwardHeader = message.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        RpcForwardAccess toAccess = networkContext.getRpcForwarder().forward(message, forwardHeader);
        if (toAccess != null && toAccess.isActive()) {
            var session = toAccess.getSession();
            rpcContext.transfer(session, forwardOperation(message));
            ForwardPoint fromPoint = new ForwardPoint(tunnel.getIdentifyToken(RpcAccessIdentify.class));
            RpcAccessPoint toPoint = toAccess.getForwardPoint();
            var content = MessageContents.copy(message)
                    .withHeader(RpcForwardHeaderBuilder.newBuilder()
                            .setFrom(fromPoint)
                            .setSender(forwardHeader.getSender())
                            .setTo(toPoint)
                            .setReceiver(forwardHeader.getReceiver())
                            .build());
            if (message.getMode() == MessageMode.REQUEST) {
                content.withHeader(RpcOriginalMessageIdHeaderBuilder.newBuilder()
                        .setMessageId(message.getId())
                        .build());
            }
            session.send(content);
            rpcContext.completeSilently();
        } else {
            rpcContext.complete(NetResultCode.RPC_SERVICE_NOT_AVAILABLE);
        }
    }

}
