package com.tny.game.net.command.dispatcher;

import com.google.common.base.MoreObjects;
import com.tny.game.common.concurrent.Waiter;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.command.auth.AuthenticateValidator;
import com.tny.game.net.exception.CommandException;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-02 04:52
 */
public class MessageDispatchCommand extends DispatchContext {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private boolean start = false;

    protected MessageDispatchCommand(MessageDispatcherContext context, MethodControllerHolder methodHolder, NetTunnel<?> tunnel, Message<?> message) {
        super(context, methodHolder, tunnel, message);
    }

    @Override
    public void execute() {
        MessageHeader header = message.getHeader();
        ControllerContext.setCurrent(this.message.getUserID(), header.getProtocolNumber());
        Throwable cause = null;
        try {
            if (isDone())
                return;
            this.fireExecuteStart();
            if (!start) {
                try {
                    // 调用逻辑业务
                    this.invoke();
                    // 调用调用结果
                    this.checkAndHandleResult(null);
                } finally {
                    this.start = true;
                }
            }
            if (!this.isDone()) {
                if (this.isWaiting()) {
                    // 检测等待者是否完成
                    this.checkWaiter();
                } else {
                    throw new CommandException(NetResultCode.DISPATCH_EXCEPTION);
                }
            }
        } catch (Throwable e) {
            cause = e;
            this.handleException(cause);
            this.checkAndHandleResult(cause);
        } finally {
            this.fireExecuteEnd(cause);
        }
    }

    /* 检测结果, 并且处理结果 */
    private void checkAndHandleResult(Throwable cause) {
        Object o = this.getResult();
        if (o instanceof Waiter) { // 如果是结果为Future进入等待逻辑
            this.waitingFor(as(o));
            return;
        }
        if (o instanceof Future) { // 如果是结果为Future进入等待逻辑
            this.waitingFor(Waiter.of(as(o)));
            return;
        }
        this.done();
        this.invokeDone(cause);
        this.handleResult();
    }

    @Override
    protected void onWaiterDone(Waiter<Object> waiter) throws Throwable {
        if (waiter.isSuccess()) {
            // 等待成功
            this.setResult(waiter.getResult());
            this.checkAndHandleResult(null);
        } else {
            // 等待异常
            throw waiter.getCause();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleException(Throwable e) {
        if (e instanceof CommandException) {
            CommandException dex = (CommandException) e;
            DISPATCHER_LOG.error(dex.getMessage(), dex);
            this.setResult(dex.getResultCode(), dex.getBody());
        } else if (e instanceof InvocationTargetException) {
            this.handleException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof ExecutionException) {
            this.handleException(e.getCause());
        } else {
            DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
            this.setResult(NetResultCode.EXECUTE_EXCEPTION);
        }
    }

    private void invoke() throws Exception {
        if (this.controller == null) {
            MessageHeader header = message.getHeader();
            DISPATCHER_LOG.warn("Controller [{}] 没有存在对应Controller ", header.getId());
            this.doneAndIntercept(NetResultCode.NO_SUCH_PROTOCOL);
            return;
        }
        //检测认证
        this.checkAuthenticate();

        String appType = this.getAppType();
        if (!controller.isActiveByAppType(appType)) {
            DISPATCHER_LOG.warn("Controller [{}] App类型 {} 无法此协议", this.getName(), appType);
            this.doneAndIntercept(NetResultCode.NO_SUCH_PROTOCOL);
            return;
        }
        String scopeType = this.getScopeType();
        if (!controller.isActiveByScope(scopeType)) {
            DISPATCHER_LOG.error("Controller [{}] Scope类型 {} 无法此协议", this.getName(), appType);
            this.doneAndIntercept(NetResultCode.NO_SUCH_PROTOCOL);
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 检测已登陆认证", this.getName());
        if (controller.isAuth() && !tunnel.isLogin()) {
            DISPATCHER_LOG.error("Controller [{}] 用户未登陆", this.getName());
            this.doneAndIntercept(NetResultCode.UNLOGIN);
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 检测用户组调用权限", this.getName());
        if (!controller.isUserGroup(tunnel.getUserType())) {
            DISPATCHER_LOG.error("Controller [{}] 用户为[{}]用户组, 无法调用此协议", this.getName(), tunnel.getUserType());
            this.doneAndIntercept(NetResultCode.NO_PERMISSIONS);
            return;
        }

        DISPATCHER_LOG.debug("Controller [{}] 执行BeforePlugins", getName());
        controller.beforeInvoke(this.tunnel, this.message, this);
        if (this.isIntercept()) {
            return;
        }
        DISPATCHER_LOG.debug("Controller [{}] 执行业务", getName());
        controller.invoke(this.tunnel, this.message, this);
    }

    /**
     * 调用完成
     *
     * @param cause
     */
    private void invokeDone(Throwable cause) {
        if (this.controller != null) {
            DISPATCHER_LOG.debug("Controller [{}] 执行AftertPlugins", getName());
            controller.afterInvoke(this.tunnel, this.message, this);
        }
        DISPATCHER_LOG.debug("Controller [{}] 处理Message完成!", getName());
        if (cause != null)
            this.fireDone(cause);
        else
            this.fireDoneSuccess();
        this.fireDone(cause);
    }

    private void checkAuthenticate() throws CommandException {
        if (!this.tunnel.isLogin()) {
            AuthenticateValidator<Object> validator = as(context.getValidator(message.getProtocol(), controller.getAuthValidator()));
            if (validator == null)
                return;
            DISPATCHER_LOG.debug("Controller [{}] 开始进行登陆认证", getName());
            Certificate<Object> certificate = validator.validate(this.tunnel, this.message);
            // 是否需要做登录校验,判断是否已经登录
            if (certificate != null && certificate.isAutherized()) {
                this.tunnel.authenticate(certificate);
                this.message.update(certificate);
                // if (this.tunnel.getMode() == TunnelMode.SERVER) {
                //     SessionKeeperFactory sessionKeeperFactory = context.getAppConfiguration().getKeeperFactory();
                //     SessionKeeper<Object> sessionKeeper = sessionKeeperFactory.getKeeper(this.tunnel.getUserType());
                //     sessionKeeper.online(this.tunnel);
                // }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void handleResult() {
        if (!this.isDone())
            return;
        MessageHeader header = message.getHeader();
        Object result = this.getResult();
        ResultCode code;
        Object body = null;
        if (result instanceof CommandResult) {
            CommandResult commandResult = (CommandResult) result;
            code = commandResult.getResultCode();
            body = commandResult.getBody();
        } else if (result instanceof ResultCode) {
            code = (ResultCode) result;
        } else {
            code = ResultCode.SUCCESS;
            body = result;
        }
        MessageContext<Object> context = null;
        switch (message.getMode()) {
            case PUSH:
                if (body != null)
                    context = MessageContexts.push(header, code, body);
                break;
            case REQUEST:
                context = MessageContexts.respond(message, code, body, message.getId());
                break;
        }
        if (context != null) {
            TunnelsUtils.responseMessage(tunnel, message, context);
        } else {
            LOGGER.info("~~~~~~~~~~~~~~~~~~~" + code.getType());
            if (code.getType() == ResultCodeType.ERROR)
                this.tunnel.close();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("message", message)
                .toString();
    }

}
