package com.tny.game.net.command.dispatcher;

import com.google.common.base.MoreObjects;
import com.tny.game.common.result.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import javax.xml.bind.ValidationException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 */
public abstract class MessageCommand<C extends CommandContext> extends DispatchContext implements Command {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private boolean start = false;

    protected C commandContext;

    protected MessageCommand(MessageDispatcherContext context, C commandContext, NetTunnel<?> tunnel, Message<?> message) {
        super(context, tunnel, message);
        this.commandContext = commandContext;
    }

    @Override
    public boolean isDone() {
        return this.commandContext.isDone();
    }

    /**
     * 身份认证
     */
    protected void authenticate(AuthenticateValidator<Object> validator) throws CommandException, ValidationException {
        if (this.tunnel.isLogin())
            return;
        if (validator == null)
            return;
        DISPATCHER_LOG.debug("Controller [{}] 开始进行登陆认证", getName());
        Certificate<Object> certificate = validator.validate(this.tunnel, this.message);
        // 是否需要做登录校验,判断是否已经登录
        if (certificate != null && certificate.isAutherized()) {
            this.message.update(certificate);
            EndpointKeeperManager keeperManager = context.getEndpointKeeperManager();
            EndpointKeeper<Object, Endpoint<Object>> endpointKeeper = keeperManager.loadOrCreate(certificate.getUserType(), tunnel.getMode());
            endpointKeeper.online(certificate, tunnel);
        }
    }

    /**
     * command 执行
     */
    @Override
    public void execute() {
        MessageHead head = message.getHead();
        ControllerContext.setCurrent(this.message.getUserId(), head.getProtocolNumber());
        MessageCommandPromise promise = commandContext.getPromise();
        Throwable cause = null;
        try {
            if (isDone())
                return;
            this.fireExecuteStart();
            if (!start) {
                try {
                    // 调用逻辑业务
                    this.invoke();
                } finally {
                    this.start = true;
                }
            }
            if (!this.isDone()) {
                if (promise.isWaiting()) {
                    // 检测等待者是否完成
                    promise.checkWait();
                }
            }
            if (this.isDone()) {
                this.handleResult();
            }
        } catch (Throwable e) {
            cause = e;
            promise.setResult(e);
            this.handleResult();
        } finally {
            this.fireExecuteEnd(cause);
        }
    }


    /**
     * 执行 invoke
     */
    protected abstract void invoke() throws Exception;

    protected abstract void invokeDone(Throwable cause);

    /**
     * 通过Throwable获取返回结果
     *
     * @param e 异常
     * @return 返回消息
     */
    private CommandResult exceptionResult(Throwable e) {
        if (e instanceof CommandException) {
            CommandException dex = (CommandException) e;
            DISPATCHER_LOG.error(dex.getMessage(), dex);
            return CommandResults.fail(dex.getResultCode(), dex.getBody());
        } else if (e instanceof InvocationTargetException) {
            return this.exceptionResult(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof ExecutionException) {
            return this.exceptionResult(e.getCause());
        } else {
            DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
            return CommandResults.fail(NetResultCode.EXECUTE_EXCEPTION);
        }
    }


    /**
     * 处理消息
     */
    private void handleResult() {
        MessageCommandPromise promise = commandContext.getPromise();
        if (!promise.isDone())
            return;
        MessageHead head = message.getHead();
        ResultCode code;
        Object body = null;
        if (promise.isSuccess()) {
            Object result = promise.getResult();
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
            this.invokeDone(null);
        } else {
            Throwable cause = promise.getCause();
            CommandResult commandResult = exceptionResult(promise.getCause());
            code = commandResult.getResultCode();
            body = commandResult.getBody();
            this.invokeDone(cause);
        }
        MessageContext<Object> context = null;
        switch (message.getMode()) {
            case PUSH:
                if (body != null)
                    context = MessageContexts.push(head, code, body);
                break;
            case REQUEST:
                context = MessageContexts.respond(message, code, body, message.getId());
                break;
        }
        if (context != null) {
            TunnelAides.responseMessage(this.tunnel, message, context);
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
