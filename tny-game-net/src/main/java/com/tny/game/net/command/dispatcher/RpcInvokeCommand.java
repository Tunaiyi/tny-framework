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

import com.google.common.base.MoreObjects;
import com.tny.game.common.concurrent.worker.*;
import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.common.runtime.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.MessageMode.*;

/**
 * <p>
 */
public class RpcInvokeCommand extends RpcHandleCommand {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcInvokeCommand.class);

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private final RpcInvokeContext invokeContext;

    private final NetMessageDispatcherContext dispatcherContext;

    private final ContactAuthenticator contactAuthenticator;

    private final boolean relay;

    private ProcessTracer tracer;

    protected RpcInvokeCommand(NetMessageDispatcherContext dispatcherContext, RpcInvokeContext invokeContext,
            ContactAuthenticator contactAuthenticator) {
        super(invokeContext.getRpcContext());
        this.dispatcherContext = dispatcherContext;
        this.contactAuthenticator = contactAuthenticator;
        this.invokeContext = invokeContext;
        var controller = invokeContext.getController();
        this.relay = controller.getMethodAnnotation(RelayTo.class) != null;
    }

    @Override
    public String getName() {
        return this.invokeContext.getName();
    }

    @Override
    public boolean isDone() {
        return this.invokeContext.isDone();
    }

    @Override
    protected void doExecute(AsyncWorker execute) throws Exception {
        enterContext.invoke(RpcTransactionContext.rpcOperation(invokeContext.getName(), enterContext.getMessage()));
        // 调用逻辑业务
        this.invoke(execute);
    }

    @Override
    protected void doDone() {
        this.handleResult();
    }

    @Override
    protected void onException(Throwable e) {
        MessageCommandPromise promise = this.invokeContext.getPromise();
        promise.setResult(e);
        fireException(e);
    }

    /**
     * 执行 invoke
     */
    private void invoke(AsyncWorker execute) throws Exception {
        var message = enterContext.getMessage();
        var tunnel = enterContext.netTunnel();
        MethodControllerHolder controller = this.invokeContext.getController();
        if (controller == null) {
            MessageHead head = message.getHead();
            DISPATCHER_LOG.warn("Controller [{}] 没有存在对应Controller ", head.getId());
            this.invokeContext.doneAndIntercept(NetResultCode.SERVER_NO_SUCH_PROTOCOL);
            return;
        }

        //检测认证：声明需登录且未认证即解析校验器（方法级→协议级→全局兜底），
        //原条件 isHasAuthValidator() 使协议级/全局注册死路（command-execution"鉴权校验器按维度生效"契约）
        if (!tunnel.isAuthenticated() && controller.isAuth()) {
            AuthenticationValidator validator = this.dispatcherContext.resolveValidator(
                    controller.isHasAuthValidator() ? controller.getAuthValidator() : null,
                    message.getHead().getProtocolId());
            contactAuthenticator.authenticate(this.dispatcherContext, enterContext, validator);
        }

        String appType = invokeContext.getAppType();
        if (!controller.isActiveByAppType(appType)) {
            DISPATCHER_LOG.warn("Controller [{}] App类型 {} 无法此协议", this.getName(), appType);
            this.invokeContext.doneAndIntercept(NetResultCode.SERVER_NO_SUCH_PROTOCOL);
            return;
        }
        String scopeType = invokeContext.getScopeType();
        if (!controller.isActiveByScope(scopeType)) {
            DISPATCHER_LOG.error("Controller [{}] Scope类型 {} 无法此协议", this.getName(), appType);
            this.invokeContext.doneAndIntercept(NetResultCode.SERVER_NO_SUCH_PROTOCOL);
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 检测已登陆认证", this.getName());
        if (controller.isAuth() && !tunnel.isAuthenticated()) {
            DISPATCHER_LOG.error("Controller [{}] 用户未登陆", this.getName());
            this.invokeContext.doneAndIntercept(NetResultCode.NO_LOGIN);
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 检测用户组调用权限", this.getName());
        if (!controller.isContactGroup(invokeContext.getContactType())) {
            DISPATCHER_LOG.error("Controller [{}] , 用户组 [{}] 无法调用此协议", this.getName(), tunnel.getGroup());
            this.invokeContext.doneAndIntercept(NetResultCode.NO_PERMISSIONS);
            return;
        }

        DISPATCHER_LOG.debug("Controller [{}] 执行BeforePlugins", getName());
        controller.beforeInvoke(tunnel, message, this.invokeContext);
        if (this.invokeContext.isIntercept()) {
            return;
        }

        DISPATCHER_LOG.debug("Controller [{}] 执行业务", getName());
        Object result = controller.invoke(tunnel, message);
        if (result instanceof CompletionStage) {
            CompletionStage<Object> stage = as(result);
            future = stage.toCompletableFuture();
            long remaining = this.invokeContext.getPromise().remainingTimeoutMillis();
            if (remaining >= 0) {
                // 异步兜底：超期以 TimeoutException 终结同一 future，whenComplete 回话串行 worker 推进队列
                future.orTimeout(remaining, TimeUnit.MILLISECONDS);
            }
            future.whenCompleteAsync((value, cause) -> {
                DISPATCHER_LOG.info("{} {} whenComplete {} {}", AbstractAsyncWorker.current(), getName(), value, cause);
                if (cause != null) {
                    this.invokeContext.setResult(cause);
                } else {
                    this.invokeContext.setResult(value);
                }
                this.onDone(cause);
            }, execute);
        } else {
            this.invokeContext.setResult(result);
        }
    }

    private void afterInvoke(Tunnel tunnel, Message message, Throwable cause) {
        MethodControllerHolder controller = this.invokeContext.getController();
        if (controller != null) {
            DISPATCHER_LOG.debug("Controller [{}] 执行AfterPlugins", getName());
            controller.afterInvoke(tunnel, message, this.invokeContext);
            DISPATCHER_LOG.debug("Controller [{}] 处理Message完成!", getName());
        }
        this.fireDone(cause);
    }

    /**
     * 处理消息
     */
    private void handleResult() {
        MessageCommandPromise promise = this.invokeContext.getPromise();
        if (!promise.isDone()) {
            return;
        }
        var message = this.enterContext.netMessage();
        var tunnel = this.enterContext.netTunnel();
        ResultCode code;
        Object body = null;
        Throwable cause = promise.getCause();
        if (promise.isSuccess()) {
            Object result = promise.getResult();
            if (result instanceof RpcResult) {
                RpcResult<?> commandResult = as(result);
                code = commandResult.resultCode();
                body = commandResult.getBody();
            } else if (result instanceof ResultCode) {
                code = (ResultCode) result;
            } else {
                code = ResultCode.SUCCESS;
                body = result;
            }
        } else {
            RpcResult<?> commandResult = resultOfException(cause);
            code = commandResult.resultCode();
            body = commandResult.getBody();
        }
        this.afterInvoke(tunnel, message, cause);
        boolean relaySuccess = relay && code.isSuccess();
        if (relaySuccess && !(tunnel instanceof RelayTunnel)) {
            // 中继隧道类型不符：降级为失败语义本地应答（修复前在此抛异常，请求悬挂且完成监听二次触发）
            code = NetResultCode.SERVER_EXECUTE_EXCEPTION;
            relaySuccess = false;
        }
        if (relaySuccess) { // 如果是协议需要继续转发, 成功时候继续转发
            RelayTunnel relayTunnel = as(tunnel);
            var monitor = invokeContext.getRpcContext().rpcMonitor();
            var rpcContext = RpcTransactionContext.createRelay(tunnel, message, monitor, false);
            relayTunnel.relay(rpcContext, false);
        }
        MessageContent content = null;
        if (shouldRespondLocally(message.getMode(), relay, relaySuccess, body != null)) {
            content = RpcMessageAide.toMessage(invokeContext.getRpcContext(), code, body);
        }
        if (content != null) {
            enterContext.complete(content, cause);
        } else {
            enterContext.completeSilently();
        }
    }

    /**
     * 本地应答判定（command-execution"每个请求恰好收到一个终答"）：
     * 请求报文除"中继成功移交"外一律本地应答（含中继失败，修复前静默悬挂）；
     * 推送保持仅在携带响应体时应答；其余模式不应答。
     */
    static boolean shouldRespondLocally(MessageMode mode, boolean relay, boolean relaySucceeded, boolean hasBody) {
        if (mode == REQUEST) {
            return !(relay && relaySucceeded);
        }
        if (mode == PUSH) {
            return hasBody;
        }
        return false;
    }

    /**
     * 通过Throwable获取返回结果
     *
     * @param e 异常
     * @return 返回消息
     */
    private RpcResult<?> resultOfException(Throwable e) {
        if (e instanceof RpcInvokeException) {
            RpcInvokeException dex = (RpcInvokeException) e;
            DISPATCHER_LOG.error(dex.getMessage(), dex);
            return RpcResults.fail(dex.getCode(), dex.getBody());
        } else if (e instanceof ResultCodableException dex) {
            DISPATCHER_LOG.error(dex.getMessage(), dex);
            return RpcResults.fail(dex.getCode());
        } else if (e instanceof InvocationTargetException) {
            return this.resultOfException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof ExecutionException || e instanceof CompletionException) {
            Throwable target = e.getCause() == null ? e : e.getCause();
            if (target instanceof TimeoutException) {
                DISPATCHER_LOG.error("Controller [{}] 异步命令超时兜底", getName(), e);
                return RpcResults.fail(NetResultCode.REQUEST_TIMEOUT);
            }
            return this.resultOfException(target);
        } else {
            DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
            return RpcResults.fail(NetResultCode.SERVER_EXECUTE_EXCEPTION);
        }
    }

    private void fireException(Throwable cause) {
        this.dispatcherContext.fireException(this, cause);
    }

    private void fireDone(Throwable cause) {
        this.dispatcherContext.fireDone(this, cause);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("message", this.invokeContext.getMessage()).toString();
    }

}
