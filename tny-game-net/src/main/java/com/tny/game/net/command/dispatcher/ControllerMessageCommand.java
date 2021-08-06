package com.tny.game.net.command.dispatcher;

import com.tny.game.common.runtime.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import javax.xml.bind.ValidationException;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.base.NetLogger.*;

/**
 * <p>
 */
public class ControllerMessageCommand extends MessageCommand<ControllerMessageCommandContext> {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    protected ControllerMessageCommand(NetTunnel<?> tunnel, MethodControllerHolder methodHolder,
            Message message, MessageDispatcherContext context, EndpointKeeperManager endpointKeeperManager) {
        super(new ControllerMessageCommandContext(methodHolder), tunnel, message, context, endpointKeeperManager);
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
            this.commandContext.doneAndIntercept(NetResultCode.NO_SUCH_PROTOCOL);
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
            this.commandContext.doneAndIntercept(NetResultCode.NO_SUCH_PROTOCOL);
            return;
        }
        String scopeType = this.getScopeType();
        if (!controller.isActiveByScope(scopeType)) {
            DISPATCHER_LOG.error("Controller [{}] Scope类型 {} 无法此协议", this.getName(), appType);
            this.commandContext.doneAndIntercept(NetResultCode.NO_SUCH_PROTOCOL);
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 检测已登陆认证", this.getName());
        if (controller.isAuth() && !this.tunnel.isLogin()) {
            DISPATCHER_LOG.error("Controller [{}] 用户未登陆", this.getName());
            this.commandContext.doneAndIntercept(NetResultCode.UNLOGIN);
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 检测用户组调用权限", this.getName());
        if (!controller.isUserGroup(this.tunnel.getUserType())) {
            DISPATCHER_LOG.error("Controller [{}] 用户为[{}]用户组, 无法调用此协议", this.getName(), this.tunnel.getUserType());
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
        Object returnResult = controller.invoke(this.tunnel, this.message, this.commandContext);
        this.commandContext.setResult(returnResult);
        tracer.done();
    }

    /**
     * 身份认证
     */
    private void checkAuthenticate(MethodControllerHolder controller) throws CommandException, ValidationException {
        if (!this.tunnel.isLogin()) {
            AuthenticateValidator<Object> validator = as(
                    this.dispatcherContext.getValidator(this.message.getProtocolId(), controller.getAuthValidator()));
            if (validator == null) {
                return;
            }
            CertificateFactory<Object> certificateFactory = as(this.tunnel.getCertificateFactory());
            DISPATCHER_LOG.debug("Controller [{}] 开始进行登陆认证", getName());
            authenticate(validator, certificateFactory);
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
            DISPATCHER_LOG.debug("Controller [{}] 执行AftertPlugins", getName());
            controller.afterInvoke(this.tunnel, this.message, this.commandContext);
            DISPATCHER_LOG.debug("Controller [{}] 处理Message完成!", getName());
            tracer.done();
        }
    }

}
