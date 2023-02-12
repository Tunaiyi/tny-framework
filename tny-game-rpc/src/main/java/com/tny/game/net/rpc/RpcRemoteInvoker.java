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
package com.tny.game.net.rpc;

import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.concurrent.TimeUnit;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.command.dispatcher.RpcTransactionContext.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 1:58 下午
 */
public class RpcRemoteInvoker {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRemoteInvoker.class);

    /**
     * 协议描述
     */
    private final RpcRemoteMethod method;

    /**
     * rpc实例
     */
    private final RpcRemoteInstance instance;

    /**
     * 远程服务
     */
    private final RpcInvokeNodeSet serviceSet;

    /**
     * 路由
     */
    private final RpcRouter router;

    /**
     * 监视器
     */
    private final RpcMonitor rpcMonitor;

    public RpcRemoteInvoker(RpcRemoteInstance instance, RpcRemoteMethod method, RpcRouter router, RpcMonitor rpcMonitor) {
        this.method = method;
        this.instance = instance;
        this.serviceSet = instance.getServiceSet();
        this.router = as(router);
        this.rpcMonitor = rpcMonitor;
    }

    private long timeout() {
        RpcRemoteSetting setting = instance.getSetting();
        return this.method.getTimeout(setting.getInvokeTimeout());
    }

    public <T> Object invoke(Object... params) {
        RpcRemoteInvokeParams invokeParams = method.getParams(params);
        RpcAccess rpcAccess = router.route(serviceSet, method, invokeParams);
        if (rpcAccess == null) {
            throw new RpcInvokeException(NetResultCode.RPC_SERVICE_NOT_AVAILABLE, "调用 {} 异常, 未找到有效的远程服务节点", this.method);
        }
        var endpoint = rpcAccess.getEndpoint();
        long timeout = timeout();
        switch (method.getMode()) {
            case PUSH:
                push(endpoint, timeout, invokeParams);
                return null;
            case REQUEST:
                return request(endpoint, timeout, invokeParams);
        }
        throw new RpcInvokeException(NetResultCode.RPC_INVOKE_FAILED, "调用 {} 异常, 非法 rpc 模式", this.method);
    }

    private Protocol protocol() {
        return Protocols.protocol(this.method.getProtocol(), this.method.getLine());
    }

    private Object getReturnObject(Message message) {
        switch (this.method.getBodyMode()) {
            case MESSAGE:
            case MESSAGE_HEAD:
                return message;
            case RESULT:
                return RpcResults.result(ResultCodes.of(message.getCode()), message.getBody());
            case BODY:
                return message.getBody();
            case RESULT_CODE_ID:
                return message.getCode();
            case RESULT_CODE:
                return ResultCodes.of(message.getCode());
        }
        throw new RpcInvokeException(NetResultCode.REMOTE_EXCEPTION, "返回类型错误");
    }

    private Object getReturnFuture(Endpoint<?> endpoint, RpcExitContext context, MessageRespondFuture future) {
        RpcPromise<Object> rpcFuture = null;
        Object returnFuture;
        if (this.method.getReturnClass().isAssignableFrom(MessageRespondFuture.class)) {
            returnFuture = future;
        } else {
            rpcFuture = new RpcPromise<>();
            returnFuture = rpcFuture;
        }
        var resultFuture = rpcFuture;
        future.whenComplete((message, cause) -> handleMessageResult(endpoint, context, message, cause, resultFuture));
        return returnFuture;
    }

    private void handleMessageResult(Endpoint<?> endpoint, RpcExitContext context, Message message, Throwable cause,
            RpcPromise<Object> rpcFuture) {
        if (cause != null) {
            if (rpcFuture != null) {
                rpcFuture.completeExceptionally(cause);
            }
            context.complete(cause);
        } else {
            if (rpcFuture != null) {
                rpcFuture.complete(endpoint, message, RpcResults.result(ResultCodes.of(message.getCode()), message.getBody()));
            }
            context.complete(message);
        }
    }

    private Object request(Endpoint<?> endpoint, long timeout, RpcRemoteInvokeParams params) {
        RequestContent content = MessageContents.request(protocol(), params.getParams());
        content.willRespondFuture(timeout)
                .withHeaders(params.getAllHeaders());
        var invokeContext = RpcTransactionContext.createExit(endpoint, content, this.method.isAsync(), rpcMonitor);
        invokeContext.invoke(rpcOperation(method.getName(), content));
        endpoint.send(content);
        MessageRespondFuture respondFuture = content.getRespondFuture();
        if (this.method.isAsync()) {
            switch (this.method.getReturnMode()) {
                case FUTURE:
                    return getReturnFuture(endpoint, invokeContext, respondFuture);
                case VOID:
                case RESULT:
                case OBJECT:
                    return null;
            }
        } else {
            try {
                Message message = respondFuture.get(timeout, TimeUnit.MILLISECONDS);
                invokeContext.complete(message);
                switch (this.method.getReturnMode()) {
                    case RESULT:
                        return RpcResults.result(ResultCodes.of(message.getCode()), message.getBody());
                    case OBJECT:
                        return getReturnObject(message);
                    case VOID:
                        return null;
                }
            } catch (Throwable cause) {
                invokeContext.complete(cause);
                handleException(cause);
            }
        }
        throw new RpcInvokeException(NetResultCode.RPC_INVOKE_FAILED, "返回类型错误");
    }

    private void push(Endpoint<?> endpoint, long timeout, RpcRemoteInvokeParams invokeParams) {
        ResultCode code = ObjectAide.ifNull(invokeParams.getCode(), NetResultCode.SUCCESS);
        MessageContent content = MessageContents.push(protocol(), code)
                .withBody(invokeParams.getBody())
                .withHeaders(invokeParams.getAllHeaders());
        var invokeContext = RpcTransactionContext.createExit(endpoint, content, false, rpcMonitor);
        invokeContext.invoke(rpcOperation(method.getName(), content));
        try {
            endpoint.send(content);
            invokeContext.complete();
            if (this.method.isAsync()) {
                return;
            }
            MessageWriteFuture awaiter = content.getWriteFuture();
            awaiter.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Throwable cause) {
            invokeContext.complete(cause);
            handleException(cause);
        }

    }

    private void handleException(Throwable e) {
        if (method.isSilently()) {
            LOGGER.warn("{} invoke exception", this.method, e);
        } else {
            ResultCode code = ResultCodeExceptionAide.codeOf(e, NetResultCode.REMOTE_EXCEPTION);
            throw new RpcInvokeException(code, e, "调用 {} 异常", this.method);
        }
    }

}
