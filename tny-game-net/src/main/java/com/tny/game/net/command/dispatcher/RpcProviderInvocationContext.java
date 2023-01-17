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

import com.tny.game.common.context.*;
import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.Executor;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/19 01:40
 **/
class RpcProviderInvocationContext extends BaseRpcInvocationContext implements RpcProviderContext {

    private final NetMessage message;

    private final NetTunnel<Object> tunnel;

    private final RpcMonitor rpcMonitor;

    private boolean silently = false;

    public RpcProviderInvocationContext(NetTunnel<?> tunnel, NetMessage message) {
        this(tunnel, message, ContextAttributes.create());
    }

    protected RpcProviderInvocationContext(NetTunnel<?> tunnel, NetMessage message, Attributes attributes) {
        super(attributes);
        this.message = message;
        this.tunnel = as(tunnel);
        if (tunnel != null) {
            this.rpcMonitor = tunnel.getContext().getRpcMonitor();
        } else {
            this.rpcMonitor = null;
        }
    }

    @Override
    public MessageSubject getMessageSubject() {
        return message;
    }

    @Override
    public RpcInvocationMode getInvocationMode() {
        return RpcInvocationMode.ENTER;
    }

    @Override
    public <U> Endpoint<U> getEndpoint() {
        return as(this.tunnel.getEndpoint());
    }

    @Override
    public Executor executor() {
        return this.getEndpoint();
    }

    @Override
    public NetMessage netMessage() {
        return this.message;
    }

    @Override
    public RpcMonitor rpcMonitor() {
        return rpcMonitor;
    }

    @Override
    public NetworkContext networkContext() {
        return tunnel.getContext();
    }

    @Override
    public <U> NetTunnel<U> netTunnel() {
        return as(this.tunnel);
    }

    @Override
    public boolean isEmpty() {
        return this.tunnel == null || this.message == null;
    }

    @Override
    public String toString() {
        return "RpcContext [" + this.message + "]";
    }

    @Override
    public boolean complete(ResultCode code, Throwable error) {
        if (tryCompleted(error)) {
            onComplete(code, null);
            return true;
        }
        return false;
    }

    @Override
    public boolean complete(ResultCode code, Object body, Throwable error) {
        if (tryCompleted(error)) {
            onComplete(code, body);
            return true;
        }
        return false;
    }

    @Override
    public boolean complete(MessageContent content, Throwable error) {
        if (tryCompleted(error)) {
            onComplete(content);
            return true;
        }
        return false;
    }

    @Override
    public boolean completeSilently(Throwable error) {
        if (tryCompleted(error)) {
            silently = true;
            onComplete();
            return true;
        }
        return false;
    }

    @Override
    public boolean completeSilently(ResultCode code, Object body) {
        if (tryCompleted(null)) {
            silently = true;
            onComplete(code, body);
            return true;
        }
        return false;
    }

    @Override
    public boolean complete() {
        return complete(ResultCode.SUCCESS);
    }

    @Override
    protected void onPrepare() {
        rpcMonitor.onBeforeInvoke(this);
    }

    @Override
    protected void onComplete() {
        if (silently) {
            this.onComplete(null);
            return;
        }
        Object body = null;
        ResultCode code = NetResultCode.SERVER_ERROR;
        if (cause instanceof NetException) {
            var exception = (NetException)cause;
            body = exception.getBody();
            code = exception.getCode();
        } else if (cause instanceof ResultCodableException) {
            var exception = (ResultCodableException)cause;
            code = exception.getCode();
        }
        this.onComplete(code, body);
    }

    private void onComplete(ResultCode code, Object body) {
        if (silently) {
            this.onComplete(null);
            return;
        }
        MessageContent content = null;
        switch (message.getMode()) {
            case RESPONSE:
            case PUSH:
                if (body != null) {
                    content = RpcMessageAide.respondMessage(message, code, body);
                }
                break;
            case REQUEST:
                content = RpcMessageAide.respondMessage(message, code, body);
                break;
        }
        this.onComplete(content);
    }

    private void onComplete(MessageContent content) {
        var cause = getCause();
        if (silently) {
            content = null;
        }
        rpcMonitor.onInvokeResult(this, content, cause);
        if (content != null) {
            RpcMessageAide.send(tunnel, content);
        }
    }

}
