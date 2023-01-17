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

import com.tny.game.common.result.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class RpcHandleContext {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcHandleContext.class);

    private final MessageCommandPromise promise;

    private final RpcProviderContext rpcContext;

    private final boolean relay;

    private RelayTunnel<Object> relayTunnel;

    private final RpcForwardHeader forward;

    protected final MethodControllerHolder controller;

    private final NetAppContext appContext;

    private boolean intercept = false;

    public RpcHandleContext(MethodControllerHolder controller, RpcProviderContext rpcContext, NetAppContext appContext) {
        this.appContext = appContext;
        this.controller = controller;
        this.rpcContext = rpcContext;
        this.promise = new MessageCommandPromise(getName());
        var message = rpcContext.netMessage();
        this.forward = message.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        this.relay = controller.getMethodAnnotation(RelayTo.class) != null;
        if (relay) {
            var tunnel = rpcContext.netTunnel();
            if (tunnel instanceof RelayTunnel) {
                relayTunnel = as(tunnel);
            } else {
                throw new RpcInvokeException(NetResultCode.SERVER_EXECUTE_EXCEPTION, "not relay tunnel");
            }
        }
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
        return rpcContext.netMessage();
    }

    public Tunnel<Object> getTunnel() {
        return rpcContext.netTunnel();
    }

    public RpcProviderContext getRpcContext() {
        return rpcContext;
    }

    public boolean isRelay() {
        return relay;
    }

    public RpcForwardHeader getForward() {
        return forward;
    }

    RelayTunnel<Object> relayTunnel() {
        return relayTunnel;
    }

    MessagerType getMessagerType() {
        var forward = this.forward;
        if (forward != null) {
            ForwardPoint servicer = forward.getFrom();
            if (servicer != null) {
                return servicer.getServiceType();
            }
        }
        return this.getTunnel().getMessagerType();
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

    /**
     * 设置运行结果Object,不中断执行
     */
    public void setVoidResult() {
        this.promise.setVoidResult();
    }

    public boolean isIntercept() {
        return this.intercept;
    }

    public boolean isDone() {
        return this.isIntercept() || this.getPromise().isDone();
    }

}
