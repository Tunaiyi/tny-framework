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

import com.tny.game.common.runtime.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.base.NetLogger.*;

/**
 * <p>
 */
public class ControllerMessageCommand extends MessageCommand {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    protected ControllerMessageCommand(NetTunnel<?> tunnel, MethodControllerHolder methodHolder,
            Message message, MessageDispatcherContext context, EndpointKeeperManager endpointKeeperManager) {
        super(new MessageCommandContext(methodHolder), tunnel, message, context, endpointKeeperManager,
                methodHolder.getMethodAnnotation(RelayTo.class) != null);
    }

    @Override
    public String getName() {
        return this.commandContext.getController().getName();
    }

    /**
     * 执行 invoke
     */
    @Override
    protected void invoke() throws Exception {
        ProcessTracer tracer = MESSAGE_EXE_INVOKE_GET_CONTROLLER_WATCHER.trace();
        MethodControllerHolder controller = this.commandContext.getController();
        if (controller == null) {
            MessageHead head = this.message.getHead();
            DISPATCHER_LOG.warn("Controller [{}] 没有存在对应Controller ", head.getId());
            this.commandContext.doneAndIntercept(NetResultCode.SERVER_NO_SUCH_PROTOCOL);
            return;
        }
        tracer.done();

        tracer = MESSAGE_EXE_INVOKE_CHECK_AUTHENTICATE_WATCHER.trace();
        //检测认证
        this.checkAuthenticate(controller);
        tracer.done();

        tracer = MESSAGE_EXE_INVOKE_CHECK_INVOKABLE_WATCHER.trace();
        String appType = this.getAppType();
        if (!controller.isActiveByAppType(appType)) {
            DISPATCHER_LOG.warn("Controller [{}] App类型 {} 无法此协议", this.getName(), appType);
            this.commandContext.doneAndIntercept(NetResultCode.SERVER_NO_SUCH_PROTOCOL);
            return;
        }
        String scopeType = this.getScopeType();
        if (!controller.isActiveByScope(scopeType)) {
            DISPATCHER_LOG.error("Controller [{}] Scope类型 {} 无法此协议", this.getName(), appType);
            this.commandContext.doneAndIntercept(NetResultCode.SERVER_NO_SUCH_PROTOCOL);
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 检测已登陆认证", this.getName());
        if (controller.isAuth() && !this.tunnel.isAuthenticated()) {
            DISPATCHER_LOG.error("Controller [{}] 用户未登陆", this.getName());
            this.commandContext.doneAndIntercept(NetResultCode.NO_LOGIN);
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 检测用户组调用权限", this.getName());
        if (!controller.isUserGroup(this.getMessagerType())) {
            DISPATCHER_LOG.error("Controller [{}] , 用户组 [{}] 无法调用此协议", this.getName(), this.tunnel.getUserGroup());
            this.commandContext.doneAndIntercept(NetResultCode.NO_PERMISSIONS);
            return;
        }
        tracer.done();

        tracer = MESSAGE_EXE_INVOKE_BEFORE_PLUGINS_WATCHER.trace();
        DISPATCHER_LOG.debug("Controller [{}] 执行BeforePlugins", getName());
        controller.beforeInvoke(this.tunnel, this.message, this.commandContext);
        if (this.commandContext.isIntercept()) {
            return;
        }
        tracer.done();

        tracer = MESSAGE_EXE_INVOKE_INVOKING_WATCHER.trace();
        DISPATCHER_LOG.debug("Controller [{}] 执行业务", getName());
        Object returnResult = controller.invoke(this.tunnel, this.message);
        Class<?> returnType = controller.getReturnType();
        if (returnType == void.class || returnType == Void.class) {
            this.commandContext.setVoidResult();
        } else {
            this.commandContext.setResult(returnResult);
        }
        tracer.done();
    }

    public MessagerType getMessagerType() {
        if (this.forward != null) {
            ForwardPoint servicer = this.forward.getFrom();
            if (servicer != null) {
                return servicer.getServiceType();
            }
        }
        return this.tunnel.getMessagerType();
    }

    /**
     * 身份认证
     */
    private void checkAuthenticate(MethodControllerHolder controller) throws CommandException, ValidationException {
        if (!this.tunnel.isAuthenticated()) {
            AuthenticateValidator<Object, CertificateFactory<Object>> validator = findValidator(controller);
            if (validator == null) {
                return;
            }
            CertificateFactory<Object> certificateFactory = as(this.tunnel.getCertificateFactory());
            DISPATCHER_LOG.debug("Controller [{}] 开始进行登陆认证", getName());
            authenticate(validator, certificateFactory);
        }
    }

    private AuthenticateValidator<Object, CertificateFactory<Object>> findValidator(MethodControllerHolder controller) {
        AuthenticateValidator<Object, CertificateFactory<Object>> validator = as(this.dispatcherContext.getValidator(controller.getAuthValidator()));
        if (validator != null) {
            return validator;
        }
        return as(this.dispatcherContext.getValidator(message.getProtocolId()));
    }

    /**
     * 身份认证
     */
    private void authenticate(AuthenticateValidator<Object, CertificateFactory<Object>> validator, CertificateFactory<Object> certificateFactory)
            throws CommandException, ValidationException {
        if (this.tunnel.isAuthenticated()) {
            return;
        }
        if (validator == null) {
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 开始进行登陆认证", getName());
        Certificate<Object> certificate = validator.validate(this.tunnel, this.message, certificateFactory);
        // 是否需要做登录校验,判断是否已经登录
        if (certificate != null && certificate.isAuthenticated()) {
            EndpointKeeper<Object, Endpoint<Object>> endpointKeeper = this.endpointKeeperManager
                    .loadEndpoint(certificate.getMessagerType(), this.tunnel.getAccessMode());
            endpointKeeper.online(certificate, this.tunnel);
        }
    }

    /**
     * 调用完成
     */
    @Override
    protected void doInvokeDone(Throwable cause) {
        MethodControllerHolder controller = this.commandContext.getController();
        if (controller != null) {
            ProcessTracer tracer = MESSAGE_EXE_INVOKE_AFTER_PLUGINS_WATCHER.trace();
            DISPATCHER_LOG.debug("Controller [{}] 执行AfterPlugins", getName());
            controller.afterInvoke(this.tunnel, this.message, this.commandContext);
            DISPATCHER_LOG.debug("Controller [{}] 处理Message完成!", getName());
            tracer.done();
        }
    }

}
