package com.tny.game.net.rpc;

import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.exception.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.concurrent.TimeUnit;

import static com.tny.game.common.utils.ObjectAide.*;

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
    private final RpcRemoterSet servicer;

    /**
     * 路由
     */
    private final RpcRouter router;

    public RpcRemoteInvoker(RpcRemoteInstance instance, RpcRemoteMethod method, RpcRouter router) {
        this.method = method;
        this.instance = instance;
        this.servicer = instance.getServiceSet();
        this.router = as(router);
    }

    private long timeout() {
        RpcRemoteSetting setting = instance.getSetting();
        return this.method.getTimeout(setting.getInvokeTimeout());
    }

    public <T> Object invoke(Object... params) {
        RpcRemoteInvokeParams invokeParams = method.getParams(params);
        RpcRemoterAccess accessPoint = router.route(servicer, method, invokeParams);
        if (accessPoint == null) {
            throw new RpcInvokeException(NetResultCode.RPC_SERVICE_NOT_AVAILABLE, "调用 {} 异常, 未找到有效的远程服务节点", this.method);
        }
        long timeout = timeout();
        switch (method.getMode()) {
            case PUSH:
                push(accessPoint, timeout, invokeParams);
                return null;
            case REQUEST:
                return request(accessPoint, timeout, invokeParams);
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

    private Object getReturnFuture(Endpoint<?> endpoint, MessageRespondAwaiter future) {
        if (this.method.getReturnClass().isAssignableFrom(MessageRespondAwaiter.class)) {
            return future;
        }
        DefaultRpcFuture<Object> rpcFuture = new DefaultRpcFuture<>();
        future.whenComplete((message, e) -> {
            if (e != null) {
                rpcFuture.completeExceptionally(e);
            } else {
                rpcFuture.complete(endpoint, message, RpcResults.result(ResultCodes.of(message.getCode()), message.getBody()));
            }
        });
        return rpcFuture;
    }

    private Object request(RpcRemoterAccess access, long timeout, RpcRemoteInvokeParams invokeParams) {
        RequestContext requestContext = MessageContexts.request(protocol(), invokeParams.getParams());
        requestContext.willRespondAwaiter(timeout)
                .withHeaders(invokeParams.getAllHeaders());
        access.send(requestContext);
        MessageRespondAwaiter awaiter = requestContext.getResponseAwaiter();
        if (this.method.isAsync()) {
            switch (this.method.getReturnMode()) {
                case FUTURE:
                    return getReturnFuture(access.getEndpoint(), awaiter);
                case VOID:
                case RESULT:
                case OBJECT:
                    return null;
            }
        } else {
            Message message;
            try {
                message = awaiter.get(timeout, TimeUnit.MILLISECONDS);
                switch (this.method.getReturnMode()) {
                    case RESULT:
                        return RpcResults.result(ResultCodes.of(message.getCode()), message.getBody());
                    case OBJECT:
                        return getReturnObject(message);
                    case VOID:
                        return null;
                }
            } catch (Throwable e) {
                handleException(e);
            }
        }
        throw new RpcInvokeException(NetResultCode.RPC_INVOKE_FAILED, "返回类型错误");
    }

    private void push(Sender sender, long timeout, RpcRemoteInvokeParams invokeParams) {
        ResultCode code = ObjectAide.ifNull(invokeParams.getCode(), NetResultCode.SUCCESS);
        MessageContext messageContext = MessageContexts.push(protocol(), code)
                .withBody(invokeParams.getBody())
                .withHeaders(invokeParams.getAllHeaders());
        sender.send(messageContext);
        if (this.method.isAsync()) {
            return;
        }
        MessageWriteAwaiter awaiter = messageContext.getWriteAwaiter();
        try {
            awaiter.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            handleException(e);
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
