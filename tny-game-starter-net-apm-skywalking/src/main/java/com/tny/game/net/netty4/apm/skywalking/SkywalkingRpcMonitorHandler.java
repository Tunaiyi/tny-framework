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
import com.tny.game.net.message.*;
import com.tny.game.net.monitor.*;
import com.tny.game.net.rpc.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.context.*;
import org.apache.skywalking.apm.agent.core.context.tag.StringTag;
import org.apache.skywalking.apm.agent.core.context.trace.*;
import org.apache.skywalking.apm.network.trace.component.OfficialComponent;
import org.slf4j.*;

import java.util.Collection;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/16 13:06
 **/
public class SkywalkingRpcMonitorHandler implements RpcMonitorReceiveHandler, RpcMonitorTransferHandler,
        RpcMonitorBeforeInvokeHandler, RpcMonitorAfterInvokeHandler, RpcMonitorResumeExecuteHandler, RpcMonitorSuspendExecuteHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkywalkingRpcMonitorHandler.class);

    private static final StringTag ARGUMENTS = new StringTag(101, "tny-rpc.arguments");

    private static final StringTag CONTACT = new StringTag(102, "tny-rpc.contact");

    private static final StringTag TARGET = new StringTag(102, "tny-rpc.target");

    private static final StringTag FORWARD = new StringTag(102, "tny-rpc.forward");

    private static final StringTag RPC_MODE = new StringTag(103, "tny-rpc.mode");

    private static final StringTag TRACE_ID = new StringTag(104, "tny-rpc.trace-id");

    private static final StringTag RPC_PROTOCOL = new StringTag(105, "tny-rpc.protocol");

    private static final StringTag SEGMENT_ID = new StringTag(107, "tny-rpc.segment-id");

    private static final StringTag SPAN_ID = new StringTag(108, "tny-rpc.span-id");

    private static final StringTag START_TIME = new StringTag(108, "tny-rpc.start-time");

    private static final StringTag END_TIME = new StringTag(108, "tny-rpc.end-time");

    private static final OfficialComponent TNY_RPC_SERVER = new OfficialComponent(165, "tny-rpc-java-server");

    private static final OfficialComponent TNY_RPC_CLIENT = new OfficialComponent(165, "tny-rpc-java-client");

    private static final AttrKey<ContextSnapshot> TRACING_SNAPSHOT = AttrKeys.key(SkywalkingRpcMonitorHandler.class, "TraceSnapshot");

    private static final AttrKey<AbstractSpan> TRACING_RPC_SPAN = AttrKeys.key(SkywalkingRpcMonitorHandler.class, "TraceRpcSpan");

    private static final AttrKey<AbstractSpan> TRACING_INVOKE_SPAN = AttrKeys.key(SkywalkingRpcMonitorHandler.class, "TraceInvokeSpan");

    private static final AttrKey<AbstractSpan> TRACING_TRANSFER_SPAN = AttrKeys.key(SkywalkingRpcMonitorHandler.class, "TraceTransferSpan");

    private final SkywalkingRpcMonitorProperties setting;

    public SkywalkingRpcMonitorHandler(SkywalkingRpcMonitorProperties setting) {
        this.setting = setting;
    }

    @Override
    public void onReceive(RpcTransactionContext rpcContext) {
        if (setting.isDisable()) {
            return;
        }
        var message = rpcContext.getMessageSubject();
        var contextCarrier = loadCarrier(message);
        AbstractSpan rpcSpan = ContextManager.createEntrySpan(rpcOperationName(message), contextCarrier);
        var contact = rpcContext.getContact();
        tagSpanService(rpcSpan, contextCarrier, contact, message);
        var snapshot = ContextManager.capture();
        var attributes = rpcContext.attributes();
        attributes.setAttribute(TRACING_SNAPSHOT, snapshot);
        if (rpcContext.isAsync()) {
            attributes.setAttribute(TRACING_RPC_SPAN, rpcSpan.prepareForAsync());
            ContextManager.stopSpan(rpcSpan);
        } else {
            attributes.setAttribute(TRACING_RPC_SPAN, rpcSpan.prepareForAsync());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("onReceive span {} {} {} | TraceId {} | SegmentId {} | SpanId {}",
                    rpcContext.getMode(), rpcSpan.getOperationName(), rpcSpan.getSpanId(),
                    contextCarrier.getTraceId(), contextCarrier.getTraceSegmentId(), contextCarrier.getSpanId());
        }
    }

    @Override
    public void onResume(RpcEnterContext rpcContext) {

    }

    @Override
    public void onSuspend(RpcEnterContext rpcContext) {
        if (setting.isDisable()) {
            return;
        }
        while (ContextManager.isActive()) {
            ContextManager.stopSpan();
        }
    }

    @Override
    public void onBeforeInvoke(RpcTransactionContext rpcContext) {
        if (setting.isDisable()) {
            return;
        }
        var span = traceOnBefore(rpcContext);
        //        if (span == null) {
        //            return;
        //        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("invoke span {} {} {}",
                    rpcContext.getMode(), span.getOperationName(), span.getSpanId());
        }
        if (rpcContext.isAsync()) {
            if (rpcContext.getMode() == RpcTransactionMode.ENTER) {
                var snapshot = ContextManager.capture();
                rpcContext.attributes().setAttribute(TRACING_SNAPSHOT, snapshot);
            }
            rpcContext.attributes().setAttribute(TRACING_INVOKE_SPAN, span.prepareForAsync());
            if (rpcContext.getMode() == RpcTransactionMode.EXIT) {
                ContextManager.stopSpan(span);
            }
        }
    }

    @Override
    public void onAfterInvoke(RpcTransactionContext rpcContext, MessageSubject result, Throwable exception) {
        if (setting.isDisable()) {
            return;
        }
        stopAsyncSpans(rpcContext, exception, TRACING_INVOKE_SPAN, TRACING_TRANSFER_SPAN, TRACING_RPC_SPAN);
        if (ContextManager.isActive()) {
            if (exception != null) {
                var span = ContextManager.activeSpan();
                span.log(exception);
            }
            ContextManager.stopSpan();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("invoke end span {}", rpcContext.getOperationName());
        }
    }

    private AbstractSpan traceOnBefore(RpcTransactionContext rpcContext) {
        AbstractSpan span;
        String operationName;
        if (rpcContext.getMode() == RpcTransactionMode.EXIT) {
            var tracingHeader = new RpcTracingHeader();
            var message = rpcContext.getMessageSubject();
            var contextCarrier = new ContextCarrier();
            operationName = remoteOperationName(rpcContext);
            span = ContextManager.createExitSpan(operationName, contextCarrier, peer(rpcContext.getContact()));
            var context = RpcContexts.current();
            if (context != null) {
                restore((RpcTransactionContext) context);
            } else {
                restore(rpcContext);
            }
            CarrierItem next = contextCarrier.items();
            while (next.hasNext()) {
                next = next.next();
                tracingHeader.put(next.getHeadKey(), next.getHeadValue());
            }
            message.putHeader(tracingHeader);
            tagSpanRemote(span, contextCarrier, rpcContext.getContact(), message);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("exit start span {} {} | {}", span.getOperationName(), span.getSpanId(), tracingHeader);
            }
        } else {
            var message = rpcContext.getMessageSubject();
            operationName = localOperationName(rpcContext);
            span = ContextManager.createLocalSpan(operationName);
            restore(rpcContext);
            tagSpanLocal(span, loadCarrier(message), rpcContext.getContact(), message);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("enter start span {} {}", span.getOperationName(), span.getSpanId());
            }
        }
        return span;
    }

    @Override
    public void onTransfer(RpcTransferContext rpcContext) {
        if (setting.isDisable()) {
            return;
        }
        AbstractSpan span;
        String operationName = transferOperationName(rpcContext);
        var message = rpcContext.getMessageSubject();
        var current = RpcContexts.current();
        boolean restore = false;
        ContextCarrier headerCarrier;
        if (rpcContext.getMode() == RpcTransactionMode.ENTER) { // Forward 情况
            var contextCarrier = new ContextCarrier();
            span = ContextManager.createExitSpan(operationName, contextCarrier, peer(rpcContext.getTo()));
            //            restore = restore((RpcTransactionContext)current);
            restore = restore(rpcContext);
            tagSpanTransfer(span, contextCarrier, rpcContext.getFrom(), message, rpcContext.getTo());
            headerCarrier = loadCarrier(message);
        } else { // Relay 情况
            if (current.isValid()) { // Relay Handle 之后再转发
                headerCarrier = new ContextCarrier();
                span = ContextManager.createExitSpan(operationName, headerCarrier, peer(rpcContext.getTo()));
                restore = restore((RpcTransactionContext) current);
            } else { // Relay 直接转发
                headerCarrier = new ContextCarrier();
                span = ContextManager.createExitSpan(operationName, headerCarrier, peer(rpcContext.getTo()));
            }
            tagSpanTransfer(span, headerCarrier, rpcContext.getFrom(), message, rpcContext.getTo());
        }
        var tracingHeader = new RpcTracingHeader();
        CarrierItem next = headerCarrier.items();
        while (next.hasNext()) {
            next = next.next();
            tracingHeader.put(next.getHeadKey(), next.getHeadValue());
        }
        message.putHeader(tracingHeader);
        rpcContext.attributes().setAttribute(TRACING_TRANSFER_SPAN, span.prepareForAsync());
        ContextManager.stopSpan(span);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("transfer start span {} {} | in command {} | restore {} | TraceId {} | SegmentId {} | SpanId {} | {}",
                    span.getOperationName(), span.getSpanId(), current.isValid(), restore,
                    headerCarrier.getTraceId(), headerCarrier.getTraceSegmentId(), headerCarrier.getSpanId(), tracingHeader);
        }
    }

    @Override
    public void onTransfered(RpcTransferContext rpcContext, MessageSubject result, Throwable exception) {
        if (setting.isDisable()) {
            return;
        }
        stopAsyncSpans(rpcContext, exception, TRACING_INVOKE_SPAN, TRACING_TRANSFER_SPAN, TRACING_RPC_SPAN);
        AbstractSpan span = null;
        if (ContextManager.isActive()) {
            if (exception != null) {
                span = ContextManager.activeSpan();
                span.log(exception);
            }
            ContextManager.stopSpan();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("transfer end span {} {}", rpcContext.getOperationName(), span == null ? null : span.getSpanId());
        }
    }

    @SuppressWarnings({"unchecked"})
    private void stopAsyncSpans(RpcTransactionContext rpcContext, Throwable cause, AttrKey<AbstractSpan>... keys) {
        for (AttrKey<AbstractSpan> key : keys) {
            stopAsyncSpan(rpcContext, cause, key);
        }
    }

    private void stopAsyncSpan(RpcTransactionContext rpcContext, Throwable cause, AttrKey<AbstractSpan> key) {
        var span = rpcContext.attributes().removeAttribute(key);
        if (span != null) {
            if (cause != null) {
                span.log(cause);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("stop {} span of {} {}", key.name(), rpcContext.getMode(), span.getOperationName());
            }
            // renewTagSpan(span, rpcContext.getContact());
            span.asyncFinish();
        }
    }

    private ContextCarrier loadCarrier(MessageSubject message) {
        var contextCarrier = new ContextCarrier();
        var header = message.getHeader(MessageHeaderConstants.RPC_TRACING);
        if (header != null) {
            LOGGER.debug("loadCarrier {} {} | {} ", message.getProtocolId(), message.getMode(), header);
            CarrierItem next = contextCarrier.items();
            while (next.hasNext()) {
                next = next.next();
                next.setHeadValue(header.get(next.getHeadKey()));
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("no {} header {}", MessageHeaderConstants.RPC_TRACING.getKey(), rpcOperationName(message));
            }
        }
        return contextCarrier;
    }

    private boolean restore(RpcTransactionContext rpcContext) {
        if (rpcContext == null || !rpcContext.isValid()) {
            return false;
        }
        return restore(rpcContext, resumeOperationName(rpcContext.getMessageSubject()));
    }

    private boolean restore(RpcTransactionContext rpcContext, String operation) {
        if (rpcContext == null || !rpcContext.isValid()) {
            return false;
        }
        AbstractSpan span = null;
        if (!ContextManager.isActive()) {
            span = ContextManager.createLocalSpan(operation);
            tagSpanService(span, null, rpcContext.getContact(), rpcContext.getMessageSubject());
        }
        boolean result = false;
        var snapshot = rpcContext.attributes().getAttribute(TRACING_SNAPSHOT);
        if (snapshot != null) {
            AbstractSpan restoreSpan = span != null ? span : ContextManager.activeSpan();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("restore span {} {} {}",
                        rpcContext.getMode(), restoreSpan.getOperationName(), restoreSpan.getSpanId());
            }
            ContextManager.continued(snapshot);
            result = true;
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("restore no snapshot {} {} {}",
                        rpcContext.getClass(), rpcContext.getMode(), rpcContext.getOperationName());
            }
        }
        if (span != null) {
            ContextManager.stopSpan(span);
        }
        return result;
    }

    private void tagSpanLocal(AbstractSpan span, ContextCarrier contextCarrier,
            NetContact contact, MessageSubject message) {
        tagSpanCommon(span, contextCarrier, NetAccessMode.SERVER, message);
        tagSpanArguments(span, message);
        tagSpanForward(span, message);
        tagSpanContact(span, CONTACT, contact);
    }

    private void tagSpanRemote(AbstractSpan span, ContextCarrier contextCarrier,
            NetContact contact, MessageSubject message) {
        tagSpanCommon(span, contextCarrier, NetAccessMode.CLIENT, message);
        tagSpanArguments(span, message);
        tagSpanForward(span, message);
        tagSpanContact(span, TARGET, contact);
    }

    private void tagSpanTransfer(AbstractSpan span, ContextCarrier contextCarrier,
            NetContact from, MessageSubject message, NetContact to) {
        tagSpanCommon(span, contextCarrier, from.getAccessMode(), message);
        tagSpanForward(span, message);
        tagSpanContact(span, CONTACT, from);
        tagSpanContact(span, TARGET, to);
    }

    private void tagSpanForward(AbstractSpan span, MessageSubject message) {
        var header = message.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        if (header == null) {
            return;
        }
        var forward = header.getTo();
        if (forward == null) {
            return;
        }
        tagSpanContact(span, FORWARD, forward);
    }

    private void tagSpanArguments(AbstractSpan span, MessageSubject message) {
        if (setting.isEnableCollectArguments()) {
            collectArguments(span, message, setting.getCollectArgumentsMaxLength());
        }
    }

    private void tagSpanService(AbstractSpan span, ContextCarrier contextCarrier, NetContact contact, MessageSubject subject) {
        tagSpanCommon(span, contextCarrier, contact.getAccessMode(), subject);
        tagSpanContact(span, CONTACT, contact);
    }

    private void tagSpanCommon(AbstractSpan span, ContextCarrier contextCarrier, NetAccessMode accessMode, MessageSubject message) {
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
        if (contextCarrier != null) {
            span.tag(TRACE_ID, contextCarrier.getTraceId());
            span.tag(SEGMENT_ID, contextCarrier.getTraceSegmentId());
            span.tag(SPAN_ID, String.valueOf(contextCarrier.getSpanId()));
        }
    }

    private String remoteOperationName(RpcTransactionContext context) {
        return createOperationName("remote:", context);
    }

    private String transferOperationName(RpcTransactionContext context) {
        return createOperationName("trans:", context);
    }

    private String localOperationName(RpcTransactionContext context) {
        return createOperationName("local:", context);
    }

    private String rpcOperationName(MessageSubject message) {
        return "tny://" + Config.Agent.SERVICE_NAME + "/" + message.getProtocolId() + "/" + message.getMode().getMark();
    }

    private String resumeOperationName(MessageSubject message) {
        return "thread:ResumeContext-" + message.getProtocolId() + "@" + message.getMode().getMark();
    }

    private String createOperationName(String action, RpcTransactionContext context) {
        String operationName;
        operationName = action + context.getOperationName();
        if (StringUtils.isBlank(operationName)) {
            operationName = rpcOperationName(context.getMessageSubject());
        }
        return operationName;
    }

    private String peer(NetContact contact) {
        if (contact == null) {
            return "NA:NA";
        }
        var address = contact.getRemoteAddress();
        return address.getHostString() + ":" + address.getPort();
    }

    private void collectArguments(AbstractSpan span, MessageSubject message, int argumentsLengthThreshold) {
        Object body = message.getBody();
        if (message.getMode() == MessageMode.REQUEST && body instanceof Collection) {
            var parameters = (Collection<?>) body;
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

    private void tagSpanContact(AbstractSpan span, StringTag key, Contact contact) {
        if (contact == null) {
            return;
        }
        span.tag(key, getContactName(contact));
    }

    private String getContactName(Contact contact) {
        return contact.getContactType().getGroup() + "[" + contact.getContactId() + "]";
    }

}
