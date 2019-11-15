package com.tny.game.net.command.dispatcher;

import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import javax.xml.bind.ValidationException;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class ControllerMessageCommand extends MessageCommand<InvokeContext> {

    private MethodControllerHolder controller;

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    protected ControllerMessageCommand(MessageDispatcherContext context, MethodControllerHolder methodHolder, NetTunnel<?> tunnel,
            Message<?> message) {
        super(context, new InvokeContext(methodHolder), tunnel, message);
        this.controller = methodHolder;
    }


    /**
     * 执行 invoke
     */
    @Override
    protected void invoke() throws Exception {
        MethodControllerHolder controller = commandContext.getController();
        if (this.controller == null) {
            MessageHead head = message.getHead();
            DISPATCHER_LOG.warn("Controller [{}] 没有存在对应Controller ", head.getId());
            commandContext.doneAndIntercept(NetResultCode.NO_SUCH_PROTOCOL);
            return;
        }
        //检测认证
        this.checkAuthenticate();

        String appType = this.getAppType();
        if (!controller.isActiveByAppType(appType)) {
            DISPATCHER_LOG.warn("Controller [{}] App类型 {} 无法此协议", this.getName(), appType);
            commandContext.doneAndIntercept(NetResultCode.NO_SUCH_PROTOCOL);
            return;
        }
        String scopeType = this.getScopeType();
        if (!controller.isActiveByScope(scopeType)) {
            DISPATCHER_LOG.error("Controller [{}] Scope类型 {} 无法此协议", this.getName(), appType);
            commandContext.doneAndIntercept(NetResultCode.NO_SUCH_PROTOCOL);
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 检测已登陆认证", this.getName());
        if (controller.isAuth() && !tunnel.isLogin()) {
            DISPATCHER_LOG.error("Controller [{}] 用户未登陆", this.getName());
            commandContext.doneAndIntercept(NetResultCode.UNLOGIN);
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 检测用户组调用权限", this.getName());
        if (!controller.isUserGroup(tunnel.getUserType())) {
            DISPATCHER_LOG.error("Controller [{}] 用户为[{}]用户组, 无法调用此协议", this.getName(), tunnel.getUserType());
            commandContext.doneAndIntercept(NetResultCode.NO_PERMISSIONS);
            return;
        }

        DISPATCHER_LOG.debug("Controller [{}] 执行BeforePlugins", getName());
        controller.beforeInvoke(this.tunnel, this.message, commandContext);
        if (commandContext.isIntercept()) {
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 执行业务", getName());
        controller.invoke(this.tunnel, this.message, commandContext);
    }

    /**
     * 身份认证
     */
    private void checkAuthenticate() throws CommandException, ValidationException {
        if (!this.tunnel.isLogin()) {
            AuthenticateValidator<Object> validator = as(context.getValidator(message.getProtocol(), controller.getAuthValidator()));
            if (validator == null)
                return;
            DISPATCHER_LOG.debug("Controller [{}] 开始进行登陆认证", getName());
            authenticate(validator);
        }
    }

    /**
     * 调用完成
     */
    @Override
    protected void invokeDone(Throwable cause) {
        if (this.controller != null) {
            DISPATCHER_LOG.debug("Controller [{}] 执行AftertPlugins", getName());
            controller.afterInvoke(this.tunnel, this.message, commandContext);
        }
        DISPATCHER_LOG.debug("Controller [{}] 处理Message完成!", getName());
        this.fireDone(cause);
    }

}
