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
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.monitor.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.agent.core.context.*;
import org.apache.skywalking.apm.agent.core.context.tag.StringTag;
import org.apache.skywalking.apm.agent.core.context.trace.*;
import org.apache.skywalking.apm.network.trace.component.OfficialComponent;
import org.slf4j.*;

import java.util.Collection;

import static com.tny.game.net.command.dispatcher.RpcInvocationContext.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/16 13:06
 **/
public class SkywalkingRpcMonitorHandler implements RpcMonitorReceiveHandler, RpcMonitorRelayHandler, RpcMonitorBeforeInvokeHandler,
        RpcMonitorInvokeResultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkywalkingRpcMonitorHandler.class);

    private static final StringTag ARGUMENTS = new StringTag(101, "tny-rpc.arguments");

    private static final StringTag MESSAGER = new StringTag(102, "tny-rpc.messager");

    private static final StringTag RPC_MODE = new StringTag(103, "tny-rpc.mode");

    private static final StringTag TRACE_ID = new StringTag(104, "tny-rpc.trace-id");

    private static final StringTag RPC_PROTOCOL = new StringTag(105, "tny-rpc.protocol");

    private static final StringTag RELAY_FROM_PROTOCOL = new StringTag(106, "tny-relay.from");

    private static final StringTag RELAY_TO_PROTOCOL = new StringTag(107, "tny-relay.to");

    private static final StringTag RELAY_MODE_PROTOCOL = new StringTag(108, "tny-relay.mode");

    private static final OfficialComponent TNY_RPC_SERVER = new OfficialComponent(166, "tny-rpc-server");

    private static final OfficialComponent TNY_RPC_CLIENT = new OfficialComponent(167, "tny-rpc-client");

    private static final AttrKey<ContextSnapshot> TRACING_SNAPSHOT = AttrKeys.key(SkywalkingRpcMonitorHandler.class, "TraceSnapshot");

    private static final AttrKey<AbstractSpan> TRACING_NET_SPAN = AttrKeys.key(SkywalkingRpcMonitorHandler.class, "TraceNetSpan");

    private static final AttrKey<AbstractSpan> TRACING_CALL_SPAN = AttrKeys.key(SkywalkingRpcMonitorHandler.class, "TraceCallSpan");

    @Override
    public void onReceive(RpcProviderContext rpcContext) {
        if (rpcContext.getInvocationMode() == RpcInvocationMode.ENTER) {
            var message = rpcContext.netMessage();
            var contextCarrier = loadCarrier(message);
            AbstractSpan span = ContextManager.createEntrySpan(rpcOperationName(message), contextCarrier);
            var tunnel = rpcContext.netTunnel();
            tagSpan(span, tunnel, message, true);
            var snapshot = ContextManager.capture();
            var attributes = rpcContext.attributes();
            attributes.setAttribute(TRACING_SNAPSHOT, snapshot);
            attributes.setAttribute(TRACING_NET_SPAN, span.prepareForAsync());
            ContextManager.stopSpan(span);
            LOGGER.debug("SERVER start span {} {}", span.getOperationName(), span.getSpanId());
        }
    }

    @Override
    public void onBeforeInvoke(RpcContext rpcContext) {
        boolean restore = !ContextManager.isActive();
        AbstractSpan span;
        String operationName;
        if (rpcContext.getInvocationMode() == RpcInvocationMode.EXIT) {
            var tracingHeader = new RpcTracingHeader();
            var message = rpcContext.getMessageSubject();
            var contextCarrier = loadCarrier(message);
            operationName = rpcOperationName(rpcContext);
            span = ContextManager.createExitSpan(operationName, contextCarrier, peer(rpcContext.getEndpoint()));
            tagSpan(span, rpcContext.getEndpoint(), message, true);
            if (restore) {
                restore(rpcContext);
            }
            CarrierItem next = contextCarrier.items();
            while (next.hasNext()) {
                next = next.next();
                tracingHeader.put(next.getHeadKey(), next.getHeadValue());
            }
            message.putHeader(tracingHeader);
            LOGGER.debug("exit start span {} {}", span.getOperationName(), span.getSpanId());
        } else {
            operationName = callOperationName(rpcContext);
            span = ContextManager.createLocalSpan(operationName);
            tagSpan(span, rpcContext.getEndpoint(), rpcContext.getMessageSubject(), false);
            if (restore) {
                restore(rpcContext);
            }
            LOGGER.debug("call start span {} {}", span.getOperationName(), span.getSpanId());
        }

        var snapshot = ContextManager.capture();
        span.prepareForAsync();
        rpcContext.attributes().setAttribute(TRACING_CALL_SPAN, span);
        rpcContext.attributes().setAttribute(TRACING_SNAPSHOT, snapshot);
        ContextManager.stopSpan(span);
    }

    @Override
    public void onInvokeResult(RpcContext rpcContext, MessageSubject result, Throwable exception) {
        var callSpan = rpcContext.attributes().getAttribute(TRACING_CALL_SPAN);
        if (callSpan != null) {
            renewTagSpan(callSpan, rpcContext.getEndpoint());
            if (exception != null) {
                callSpan.log(exception);
            }
            callSpan.asyncFinish();
        }
        var netSpan = rpcContext.attributes().getAttribute(TRACING_NET_SPAN);
        if (netSpan != null) {
            renewTagSpan(netSpan, rpcContext.getEndpoint());
            if (exception != null) {
                netSpan.log(exception);
            }
            netSpan.asyncFinish();
        }
        if (ContextManager.isActive()) {
            ContextManager.stopSpan();
        }
    }

    @Override
    public void onRelay(NetTunnel<?> from, NetRelayLink to, Message message) {
        var contextCarrier = new ContextCarrier();
        var tracingHeader = new RpcTracingHeader();
        AbstractSpan span = ContextManager.createEntrySpan(relayOperation(message), contextCarrier);
        CarrierItem next = contextCarrier.items();
        while (next.hasNext()) {
            next = next.next();
            tracingHeader.put(next.getHeadKey(), next.getHeadValue());
        }
        message.putHeader(tracingHeader);
        relayTagSpan(span, from, to, message, false);
        LOGGER.debug("relay span {} {}", span.getOperationName(), span.getSpanId());
    }

    @Override
    public void onRelay(NetRelayLink from, NetTunnel<?> to, Message message) {
        var contextCarrier = new ContextCarrier();
        var tracingHeader = new RpcTracingHeader();
        AbstractSpan span = ContextManager.createEntrySpan(relayOperation(message), contextCarrier);
        CarrierItem next = contextCarrier.items();
        while (next.hasNext()) {
            next = next.next();
            tracingHeader.put(next.getHeadKey(), next.getHeadValue());
        }
        message.putHeader(tracingHeader);
        relayTagSpan(span, from, to, message, false);
        LOGGER.debug("relay span {} {}", span.getOperationName(), span.getSpanId());
    }

    private ContextCarrier loadCarrier(MessageSubject message) {
        var contextCarrier = new ContextCarrier();
        var header = message.getHeader(MessageHeaderConstants.RPC_TRACING);
        if (header != null) {
            CarrierItem next = contextCarrier.items();
            while (next.hasNext()) {
                next = next.next();
                next.setHeadValue(header.get(next.getHeadKey()));
            }
        }
        return contextCarrier;
    }

    private void restore(RpcContext rpcContext) {
        var snapshot = rpcContext.attributes().getAttribute(TRACING_SNAPSHOT);
        if (snapshot != null) {
            ContextManager.continued(snapshot);
        }
    }

    private void tagSpan(AbstractSpan span, Communicator<?> communicator, MessageSubject message, boolean collectArguments) {
        tagSpan(span, communicator.getAccessMode(), getMessagerName(communicator), message, collectArguments);
    }

    private void tagSpan(AbstractSpan span, NetAccessMode accessMode, String messagerName, MessageSubject message,
            boolean collectArguments) {
        if (accessMode == NetAccessMode.SERVER) {
            span.setComponent(TNY_RPC_SERVER);
        } else {
            span.setComponent(TNY_RPC_CLIENT);
        }
        SpanLayer.asRPCFramework(span);
        var mode = message.getMode().name();
        var protocolId = String.valueOf(message.getProtocolId());
        span.tag(RPC_MODE, mode);
        span.tag(RPC_PROTOCOL, protocolId);
        span.tag(MESSAGER, messagerName);
        span.tag(TRACE_ID, ContextManager.getGlobalTraceId());
        if (collectArguments) {
            collectArguments(span, message, 1024);
        }
    }

    private void relayTagSpan(AbstractSpan span, NetTunnel<?> from, NetRelayLink to, MessageSubject message, boolean collectArguments) {
        tagSpan(span, to.getAccessMode(), to.getService() + to.getInstanceId(), message, collectArguments);
        String fromMessager = getMessagerName(from);
        span.tag(RELAY_FROM_PROTOCOL, fromMessager);
        String toMessager = to.getId();
        span.tag(RELAY_TO_PROTOCOL, toMessager);
        span.tag(RELAY_MODE_PROTOCOL, "IN");
    }

    private void relayTagSpan(AbstractSpan span, NetRelayLink from, NetTunnel<?> to, MessageSubject message, boolean collectArguments) {
        tagSpan(span, to, message, collectArguments);
        String fromMessager = from.getId();
        span.tag(RELAY_FROM_PROTOCOL, fromMessager);
        String toMessager = getMessagerName(to);
        span.tag(RELAY_TO_PROTOCOL, toMessager);
        span.tag(RELAY_MODE_PROTOCOL, "OUT");
    }

    private void renewTagSpan(AbstractSpan span, Communicator<?> communicator) {
        String messager = getMessagerName(communicator);
        span.tag(MESSAGER, messager);
    }

    private String rpcOperationName(RpcContext context) {
        return createOperationName("rpc", context);
    }

    private String callOperationName(RpcContext context) {
        return createOperationName("call", context);
    }

    private String createOperationName(String action, RpcContext context) {
        String operationName = null;
        if (context instanceof RpcInvocationContext) {
            operationName = action + ((RpcInvocationContext)context).getOperationName();
        }
        if (StringUtils.isBlank(operationName)) {
            operationName = rpcOperationName(context.getMessageSubject());
        }
        return operationName;
    }

    private String rpcOperationName(MessageSubject message) {
        return "tny://" + message.getProtocolId() + "/" + message.getMode().getMark();
    }

    private String peer(Endpoint<?> endpoint) {
        var address = endpoint.getRemoteAddress();
        return address.getHostString() + ":" + address.getPort();
    }

    private String peer(NetRelayLink link) {
        var address = link.getRemoteAddress();
        return address.getHostString() + ":" + address.getPort();
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
