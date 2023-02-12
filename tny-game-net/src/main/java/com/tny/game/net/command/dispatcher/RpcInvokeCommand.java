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

import com.google.common.base.MoreObjects;
import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.common.runtime.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

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

    private final MessagerAuthenticator messagerAuthenticator;

    private final boolean relay;

    private ProcessTracer tracer;

    protected RpcInvokeCommand(NetMessageDispatcherContext dispatcherContext, RpcInvokeContext invokeContext,
            MessagerAuthenticator messagerAuthenticator) {
        super(invokeContext.getRpcContext());
        this.dispatcherContext = dispatcherContext;
        this.messagerAuthenticator = messagerAuthenticator;
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
    protected void onStartTick() {
        this.fireExecuteStart();
    }

    @Override
    protected void onRun() throws Exception {
        rpcContext.invoke(RpcTransactionContext.rpcOperation(invokeContext.getName(), rpcContext.netMessage()));
        // 调用逻辑业务
        this.invoke();
    }

    @Override
    protected void onDone(Throwable cause) {
        this.handleResult();
    }

    @Override
    protected void onTick() {
        MessageCommandPromise promise = this.invokeContext.getPromise();
        if (promise.isWaiting()) {
            // 检测等待者是否完成
            promise.checkWait();
        }
    }

    @Override
    protected void onEndTick(Throwable cause) {
        this.fireExecuteEnd(cause);
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
    private void invoke() throws Exception {
        var message = rpcContext.netMessage();
        var tunnel = rpcContext.netTunnel();
        MethodControllerHolder controller = this.invokeContext.getController();
        if (controller == null) {
            MessageHead head = message.getHead();
            DISPATCHER_LOG.warn("Controller [{}] 没有存在对应Controller ", head.getId());
            this.invokeContext.doneAndIntercept(NetResultCode.SERVER_NO_SUCH_PROTOCOL);
            return;
        }

        //检测认证
        if (!tunnel.isAuthenticated() && controller.isHasAuthValidator()) {
            messagerAuthenticator.authenticate(this.dispatcherContext, rpcContext, controller.getAuthValidator());
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
        if (!controller.isUserGroup(invokeContext.getMessagerType())) {
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
        this.invokeContext.setResult(result);
    }

    private void afterInvoke(Tunnel<Object> tunnel, Message message, Throwable cause) {
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
        var message = this.rpcContext.netMessage();
        var tunnel = this.rpcContext.netTunnel();
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
                code = (ResultCode)result;
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
        MessageContent content = null;
        if ((message.getMode() == REQUEST && !relay) || (message.getMode() == PUSH && body != null)) {
            content = RpcMessageAide.toMessage(invokeContext.getRpcContext(), code, body);
        }
        if (relay && code.isSuccess()) { // 如果是协议需要继续转发, 成功时候继续转发
            if (tunnel instanceof RelayTunnel) {
                var relayTunnel = (RelayTunnel<?>)tunnel;
                var monitor = invokeContext.getRpcContext().rpcMonitor();
                var rpcContext = RpcTransactionContext.createRelay(tunnel, message, monitor, false);
                relayTunnel.relay(rpcContext, false);
            } else {
                throw new RpcInvokeException(NetResultCode.SERVER_EXECUTE_EXCEPTION, "not relay tunnel");
            }
        }
        if (content != null) {
            rpcContext.complete(content, cause);
        } else {
            rpcContext.completeSilently();
        }
    }

    /**
     * 通过Throwable获取返回结果
     *
     * @param e 异常
     * @return 返回消息
     */
    private RpcResult<?> resultOfException(Throwable e) {
        if (e instanceof RpcInvokeException) {
            RpcInvokeException dex = (RpcInvokeException)e;
            DISPATCHER_LOG.error(dex.getMessage(), dex);
            return RpcResults.fail(dex.getCode(), dex.getBody());
        } else if (e instanceof ResultCodableException) {
            ResultCodableException dex = (ResultCodableException)e;
            DISPATCHER_LOG.error(dex.getMessage(), dex);
            return RpcResults.fail(dex.getCode());
        } else if (e instanceof InvocationTargetException) {
            return this.resultOfException(((InvocationTargetException)e).getTargetException());
        } else if (e instanceof ExecutionException) {
            return this.resultOfException(e.getCause());
        } else {
            DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
            return RpcResults.fail(NetResultCode.SERVER_EXECUTE_EXCEPTION);
        }
    }

    private void fireExecuteStart() {
        this.dispatcherContext.fireExecuteStart(this);
    }

    private void fireException(Throwable cause) {
        this.dispatcherContext.fireException(this, cause);
    }

    private void fireExecuteEnd(Throwable cause) {
        this.dispatcherContext.fireExecuteEnd(this, cause);
    }

    private void fireDone(Throwable cause) {
        this.dispatcherContext.fireDone(this, cause);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("message", this.invokeContext.getMessage()).toString();
    }

}
