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

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import com.tny.game.net.monitor.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.List;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/11/7 04:53
 **/
@Unit
@UnitInterface
public class RpcMonitor {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcMonitor.class);

    private final List<RpcMonitorBeforeInvokeHandler> beforeInvokeHandlers;

    private final List<RpcMonitorAfterInvokeHandler> afterInvokeHandlers;

    private final List<RpcMonitorReceiveHandler> receiveHandlers;

    private final List<RpcMonitorRelayHandler> relayHandlers;

    private final List<RpcMonitorSendHandler> sendHandlers;

    public RpcMonitor() {
        beforeInvokeHandlers = null;
        afterInvokeHandlers = null;
        receiveHandlers = null;
        relayHandlers = null;
        sendHandlers = null;
    }

    public RpcMonitor(
            List<RpcMonitorReceiveHandler> receiveHandlers,
            List<RpcMonitorSendHandler> sendHandlers,
            List<RpcMonitorRelayHandler> relayHandlers,
            List<RpcMonitorAfterInvokeHandler> afterInvokeHandlers,
            List<RpcMonitorBeforeInvokeHandler> beforeInvokeHandlers) {
        this.beforeInvokeHandlers = beforeInvokeHandlers == null ? List.of() : List.copyOf(beforeInvokeHandlers);
        this.afterInvokeHandlers = afterInvokeHandlers == null ? List.of() : List.copyOf(afterInvokeHandlers);
        this.receiveHandlers = receiveHandlers == null ? List.of() : List.copyOf(receiveHandlers);
        this.relayHandlers = relayHandlers == null ? List.of() : List.copyOf(relayHandlers);
        this.sendHandlers = sendHandlers == null ? List.of() : List.copyOf(sendHandlers);
    }

    public void onReceive(RpcProviderContext rpcContext) {
        var tunnel = rpcContext.netTunnel();
        var message = rpcContext.netMessage();
        for (var handler : receiveHandlers) {
            try {
                handler.onReceive(rpcContext);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
        NetLogger.logReceive(tunnel, message);
    }

    public void onSend(NetTunnel<?> tunnel, Message message) {
        NetLogger.logSend(tunnel, message);
        for (var handler : sendHandlers) {
            try {
                handler.onSend(tunnel, message);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    public void onRelay(NetRelayLink from, NetTunnel<?> to, Message message) {
        for (var handler : relayHandlers) {
            try {
                handler.onRelay(from, to, message);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    public void onRelay(NetTunnel<?> from, NetRelayLink to, Message message) {
        for (var handler : relayHandlers) {
            try {
                handler.onRelay(from, to, message);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    void onBeforeInvoke(RpcContext rpcContext) {
        for (var handler : beforeInvokeHandlers) {
            try {
                handler.onBeforeInvoke(rpcContext);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    void onAfterInvoke(RpcContext rpcContext, MessageSubject result, Throwable exception) {
        for (var handler : afterInvokeHandlers) {
            try {
                handler.onAfterInvoke(rpcContext, result, exception);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

}
