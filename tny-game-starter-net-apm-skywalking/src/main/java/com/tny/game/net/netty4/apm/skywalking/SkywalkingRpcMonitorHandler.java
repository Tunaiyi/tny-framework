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
package com.tny.game.net.netty4.apm.skywalking;

import com.tny.game.common.context.*;
import com.tny.game.common.url.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.monitor.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.apache.skywalking.apm.agent.core.context.*;
import org.apache.skywalking.apm.agent.core.context.tag.*;
import org.apache.skywalking.apm.agent.core.context.trace.*;
import org.apache.skywalking.apm.network.trace.component.OfficialComponent;

import java.util.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/16 13:06
 **/
public class SkywalkingRpcMonitorHandler implements RpcMonitorReceiveHandler, RpcMonitorBeforeInvokeHandler, RpcMonitorRelayHandler,
        RpcMonitorAfterInvokeHandler {

    private static final StringTag ARGUMENTS = new StringTag(101, "tny-rpc.arguments");

    private static final StringTag MESSAGER = new StringTag(103, "tny-rpc.messager");

    private static final StringTag RPC_MODE = new StringTag(102, "tny-rpc.mode");

    private static final StringTag RPC_PROTOCOL_ID = new StringTag(104, "tny-rpc.protocol-id");

    private static final StringTag RPC_PROTOCOL = new StringTag(105, "tny-rpc.protocol");

    private static final OfficialComponent TNY_RPC = new OfficialComponent(166, "tny-rpc");

    private static final AttrKey<ContextSnapshot> TRACING_SNAPSHOT = AttrKeys.key(SkywalkingRpcMonitorHandler.class, "TracingSnapshot");

    @Override
    public void onReceive(RpcProviderContext rpcContext) {
        var tunnel = rpcContext.netTunnel();
        var networkContext = tunnel.getContext();
        var accessMode = networkContext.getAccessMode();
        AbstractSpan span;
        if (Objects.requireNonNull(accessMode) == NetAccessMode.SERVER) {
            var contextCarrier = new ContextCarrier();
            var message = rpcContext.netMessage();
            var header = message.getHeader(MessageHeaderConstants.RPC_TRACING);
            if (header != null) {
                CarrierItem next = contextCarrier.items();
                while (next.hasNext()) {
                    next = next.next();
                    next.setHeadValue(header.get(next.getHeadKey()));
                }
            }
            span = ContextManager.createEntrySpan(getOperationName(message), contextCarrier);
            tagSpan(span, tunnel, message, true);
            var snapshot = ContextManager.capture();
            rpcContext.attributes().setAttribute(TRACING_SNAPSHOT, snapshot);
        }
    }

    @Override
    public void onBeforeInvoke(RpcContext rpcContext) {
        restore(rpcContext);
        var endpoint = (NetEndpoint<?>)rpcContext.getEndpoint();
        var networkContext = endpoint.getContext();
        var accessMode = networkContext.getAccessMode();
        AbstractSpan span;
        if (Objects.requireNonNull(accessMode) == NetAccessMode.CLIENT) {
            String peer = "";
            if (endpoint instanceof Terminal) {
                peer = peer(((Terminal<?>)endpoint).getUrl());
            }
            var contextCarrier = new ContextCarrier();
            var message = rpcContext.getMessageSubject();
            var tracingHeader = new RpcTracingHeader();
            span = ContextManager.createExitSpan(getOperationName(message), contextCarrier, peer);
            CarrierItem next = contextCarrier.items();
            while (next.hasNext()) {
                next = next.next();
                tracingHeader.put(next.getHeadKey(), next.getHeadValue());
            }
            message.putHeader(tracingHeader);
            tagClientSpan(span, endpoint, message, true);
            var snapshot = ContextManager.capture();
            rpcContext.attributes().setAttribute(TRACING_SNAPSHOT, snapshot);
        }
    }

    @Override
    public void onRelay(NetTunnel<?> from, NetRelayLink to, Message message) {
        var endpoint = from.getEndpoint();
        var networkContext = endpoint.getContext();
        var accessMode = networkContext.getAccessMode();
        AbstractSpan span;
        if (Objects.requireNonNull(accessMode) == NetAccessMode.CLIENT) {
            String peer = "";
            if (endpoint instanceof Terminal) {
                peer = peer(((Terminal<?>)endpoint).getUrl());
            }
            var contextCarrier = new ContextCarrier();
            var tracingHeader = new RpcTracingHeader();
            span = ContextManager.createExitSpan(getOperationName(message), contextCarrier, peer);
            CarrierItem next = contextCarrier.items();
            while (next.hasNext()) {
                next = next.next();
                tracingHeader.put(next.getHeadKey(), next.getHeadValue());
            }
            message.putHeader(tracingHeader);
            tagClientSpan(span, endpoint, message, true);
        }
    }

    @Override
    public void onRelay(NetRelayLink from, NetTunnel<?> to, Message message) {

    }

    @Override
    public void onAfterInvoke(RpcContext rpcContext, MessageSubject result, Throwable exception) {
        restore(rpcContext);
        if (result != null && exception != null) {
            AbstractSpan span = ContextManager.activeSpan();
            span.log(exception);
        }
        ContextManager.stopSpan();
    }

    private void restore(RpcContext rpcContext) {
        if (!ContextManager.isActive()) {
            var snapshot = rpcContext.attributes().getAttribute(TRACING_SNAPSHOT);
            if (snapshot != null) {
                ContextManager.continued(snapshot);
            }
        }
    }

    private void tagSpan(AbstractSpan span, Communicator<?> communicator, MessageSubject message, boolean collectArguments) {
        span.setComponent(TNY_RPC);
        SpanLayer.asRPCFramework(span);
        var mode = message.getMode().name();
        var protocolId = String.valueOf(message.getProtocolId());
        span.tag(RPC_MODE, mode);
        span.tag(RPC_PROTOCOL_ID, protocolId);
        span.tag(RPC_PROTOCOL, protocolId + "-" + mode);
        String messager = getMessagerName(communicator);
        span.tag(MESSAGER, messager);
        if (collectArguments) {
            collectArguments(span, message, 16);
        }
    }

    private void tagClientSpan(AbstractSpan span, Communicator<?> communicator, MessageSubject message, boolean collectArguments) {
        tagSpan(span, communicator, message, collectArguments);
        span.tag(Tags.URL, generateURL(communicator, message));
    }

    private String getOperationName(MessageSubject message) {
        var mode = message.getMode();
        return mode + "@" + message.getProtocolId();
    }

    private String peer(URL url) {
        return url.getHost() + ":" + url.getPort();
    }

    /**
     * Format request url. e.g. tny://127.0.0.1:20880/1000/REQUEST(String)
     *
     * @return request url.
     */
    private String generateURL(Communicator<?> communicator, MessageSchema message) {
        var address = communicator.getRemoteAddress();
        return "tny://" + address.getHostString() + ":" + address.getPort() + "/" + message.getProtocolId() + "/" + message.getMode();
    }

    private void collectArguments(AbstractSpan span, MessageSubject message, int argumentsLengthThreshold) {
        Object body = message.getBody();
        if (message.getMode() == MessageMode.REQUEST && body instanceof Collection) {
            var parameters = (Collection<?>)body;
            StringBuilder stringBuilder = new StringBuilder();
            boolean first = true;
            for (var parameter : parameters) {
                if (!first) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(parameter);
                if (stringBuilder.length() > argumentsLengthThreshold) {
                    stringBuilder.append("...");
                    break;
                }
                first = false;
            }
            span.tag(ARGUMENTS, stringBuilder.toString());
        }
    }

    private String getMessagerName(Communicator<?> communicator) {
        return communicator.getMessagerType().getGroup() + "-" + communicator.getMessagerId();
    }

}
