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

import com.tny.game.common.result.*;
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

/**
 * <p>
 */
public class RpcInvokeContext {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcInvokeContext.class);

    private final MessageCommandPromise promise;

    private final RpcEnterContext rpcContext;

    private final RpcForwardHeader forward;

    private final MethodControllerHolder controller;

    private final NetAppContext appContext;

    private boolean intercept = false;

    /**
     * 异步命令兜底时限：取会话执行器工厂配置，链路任一环节缺省回落 3000ms。
     */
    private static long resolveCommandTimeout(RpcEnterContext rpcContext) {
        try {
            var factory = rpcContext.networkContext().getCommandExecutorFactory();
            if (factory != null) {
                return factory.getCommandTimeoutMillis();
            }
        } catch (Throwable ignored) {
            // 装配不全的最小上下文（含测试桩）回落默认
        }
        return 3000L;
    }

    public RpcInvokeContext(MethodControllerHolder controller, RpcEnterContext rpcContext, NetAppContext appContext) {
        this.appContext = appContext;
        this.controller = controller;
        this.rpcContext = rpcContext;
        this.promise = new MessageCommandPromise(getName(), resolveCommandTimeout(rpcContext));
        var message = rpcContext.getMessage();
        this.forward = message.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
    }

    /**
     * @return 获取名字
     */
    public String getName() {
        return this.controller.getSimpleName();
    }

    public String getAppType() {
        return this.appContext.getAppType();
    }

    public String getScopeType() {
        return this.appContext.getScopeType();
    }

    public Message getMessage() {
        return rpcContext.getMessage();
    }

    public Tunnel getTunnel() {
        return rpcContext.netTunnel();
    }

    public RpcEnterContext getRpcContext() {
        return rpcContext;
    }

    ContactType getContactType() {
        var forward = this.forward;
        if (forward != null) {
            ForwardPoint servicer = forward.getFrom();
            if (servicer != null) {
                return servicer.getServiceType();
            }
        }
        return this.getTunnel().getContactType();
    }

    /**
     * @return 获取结果
     */
    public Object getResult() {
        return this.promise.getResult();
    }

    public MethodControllerHolder getController() {
        return this.controller;
    }

    protected MessageCommandPromise getPromise() {
        return this.promise;
    }

    /**
     * 设置CommandResult,并中断执行
     *
     * @param result 运行结果
     */
    public void doneAndIntercept(RpcResult<?> result) {
        if (!this.intercept) {
            this.intercept = true;
            this.setResult(result);
        }
    }

    /**
     * 设置结果码,并中断执行
     *
     * @param code 结果码
     */

    public void doneAndIntercept(ResultCode code) {
        if (!this.intercept) {
            this.intercept = true;
            this.setResult(code);
        }
    }

    /**
     * 设置CommandResult,不中断执行
     *
     * @param result 运行结果
     */

    public void setResult(RpcResult<?> result) {
        this.promise.setResult(result);
    }

    /**
     * 设置结果码,不中断执行
     *
     * @param code 结果码
     */
    public void setResult(ResultCode code) {
        this.promise.setResult(code);
    }

    /**
     * 设置结果码与消息体,不中断执行
     *
     * @param code 结果码
     * @param body 消息体
     */
    public void setResult(ResultCode code, Object body) {
        this.promise.setResult(RpcResults.result(code, body));
    }

    /**
     * 设置运行结果Object,不中断执行
     *
     * @param result 消息
     */
    public void setResult(Object result) {
        this.promise.setResult(result);
    }

    public boolean isIntercept() {
        return this.intercept;
    }

    public boolean isDone() {
        return this.isIntercept() || this.getPromise().isDone();
    }

}
