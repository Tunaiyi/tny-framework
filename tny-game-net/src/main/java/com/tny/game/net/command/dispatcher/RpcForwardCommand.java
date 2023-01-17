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
import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import static com.tny.game.net.command.dispatcher.RpcInvocationContext.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/3 03:42
 **/
public class RpcForwardCommand extends BaseCommand implements RpcCommand {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcForwardCommand.class);

    private final RpcProviderContext rpcContext;

    public RpcForwardCommand(RpcProviderContext rpcContext) {
        this.rpcContext = rpcContext;
    }

    @Override
    protected void action() throws Throwable {
        Throwable exception = null;
        for (int time = 0; time < 5; time++) {
            try {
                RpcContexts.setCurrent(rpcContext);
                forward();
                return;
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
        var message = rpcContext.netMessage();
        var networkContext = rpcContext.networkContext();
        rpcContext.prepare(forwardOperation(message));
        RpcForwardHeader forwardHeader = message.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        RpcForwardAccess toAccess = networkContext.getRpcForwarder().forward(message, forwardHeader);
        if (toAccess != null && toAccess.isActive()) {
            ForwardPoint fromPoint = new ForwardPoint(tunnel.getUserId());
            RpcAccessPoint toPoint = toAccess.getForwardPoint();
            var content = MessageContents.copy(message)
                    .withHeader(RpcForwardHeaderBuilder.newBuilder()
                            .setFrom(fromPoint)
                            .setSender(forwardHeader.getSender())
                            .setTo(toPoint)
                            .setReceiver(forwardHeader.getReceiver())
                            .build())
                    .withHeader(RpcOriginalMessageIdHeaderBuilder.newBuilder()
                            .setMessageId(message.getId())
                            .build());
            var endPoint = toAccess.getEndpoint();
            var forwardContext = RpcConsumerContext.create(endPoint, content, rpcContext.rpcMonitor());
            forwardContext.prepare(towardOperation(content)); // TODO 是否在 RpcConsumerContext 处理逻辑??
            endPoint.send(content);
            forwardContext.complete();
            rpcContext.completeSilently();
        } else {
            rpcContext.complete(NetResultCode.RPC_SERVICE_NOT_AVAILABLE);
        }
    }

}
