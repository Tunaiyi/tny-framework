package com.tny.game.net.command.dispatcher;

import com.google.common.base.MoreObjects;
import com.tny.game.common.result.*;
import com.tny.game.common.runtime.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import javax.xml.bind.ValidationException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

import static com.tny.game.common.runtime.TrackPrintOption.*;
import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.base.NetLogger.*;

/**
 * <p>
 */
public abstract class MessageCommand<C extends MessageCommandContext> implements Command {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageCommand.class);

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private boolean start = false;

    protected C commandContext;

    protected NetTunnel<Object> tunnel;

    protected NetMessage message;

    protected MessageDispatcherContext dispatcherContext;

    protected EndpointKeeperManager endpointKeeperManager;

    public String getAppType() {
        return this.dispatcherContext.getAppContext().getAppType();
    }

    public String getScopeType() {
        return this.dispatcherContext.getAppContext().getScopeType();
    }

    protected MessageCommand(C commandContext, NetTunnel<?> tunnel, Message message,
            MessageDispatcherContext dispatcherContext, EndpointKeeperManager endpointKeeperManager) {
        this.tunnel = as(tunnel);
        this.message = as(message);
        this.dispatcherContext = dispatcherContext;
        this.endpointKeeperManager = endpointKeeperManager;
        this.commandContext = commandContext;
    }

    public Message getMessage() {
        return this.message;
    }

    @Override
    public String getName() {
        return this.commandContext.getName();
    }

    @Override
    public boolean isDone() {
        return this.commandContext.isDone();
    }

    /**
     * 身份认证
     */
    protected void authenticate(AuthenticateValidator<Object> validator, CertificateFactory<Object> certificateFactory)
            throws CommandException, ValidationException {
        if (this.tunnel.isLogin()) {
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
                    .loadOrCreate(certificate.getUserType(), this.tunnel.getMode());
            endpointKeeper.online(certificate, this.tunnel);
        }
    }

    /**
     * command 执行
     */
    @Override
    public void execute() {
        try (ProcessTracer ignored = ProcessWatcher.of(this.getName(), CLOSE, (w) -> w.schedule(15, TimeUnit.SECONDS)).trace()) {
            Throwable cause = null;
            MessageHead head = this.message.getHead();
            ControllerContext.setCurrent(head.getProtocolId());
            MessageCommandPromise promise = this.commandContext.getPromise();
            ProcessTracer processTracer;
            try {
                if (isDone()) {
                    return;
                }
                this.fireExecuteStart();
                if (!this.start) {
                    processTracer = MESSAGE_EXE_INVOKE_WATCHER.trace();
                    try {
                        // 调用逻辑业务
                        this.invoke();
                    } finally {
                        this.start = true;
                    }
                    processTracer.done();
                }
                if (!this.isDone()) {
                    if (promise.isWaiting()) {
                        // 检测等待者是否完成
                        promise.checkWait();
                    }
                }
                processTracer = MESSAGE_EXE_HANDLE_RESULT_WATCHER.trace();
                if (this.isDone()) {
                    this.handleResult();
                }
                processTracer.done();
            } catch (Throwable e) {
                cause = e;
                promise.setResult(e);
                this.handleResult();
            } finally {
                this.fireExecuteEnd(cause);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 执行 invoke
     */
    protected abstract void invoke() throws Exception;

    private void invokeDone(Throwable cause) {
        doInvokeDone(cause);
        this.fireDone(cause);
        traceDone(NET_TRACE_INPUT_ALL_ATTR, this.message);
    }

    protected abstract void doInvokeDone(Throwable cause);

    /**
     * 通过Throwable获取返回结果
     *
     * @param e 异常
     * @return 返回消息
     */
    private CommandResult exceptionResult(Throwable e) {
        if (e instanceof CommandException) {
            CommandException dex = (CommandException)e;
            DISPATCHER_LOG.error(dex.getMessage(), dex);
            return CommandResults.fail(dex.getResultCode(), dex.getBody());
        } else if (e instanceof InvocationTargetException) {
            return this.exceptionResult(((InvocationTargetException)e).getTargetException());
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
        MessageCommandPromise promise = this.commandContext.getPromise();
        if (!promise.isDone()) {
            return;
        }
        MessageHead head = this.message.getHead();
        ResultCode code;
        Object body = null;
        if (promise.isSuccess()) {
            Object result = promise.getResult();
            if (result instanceof CommandResult) {
                CommandResult commandResult = (CommandResult)result;
                code = commandResult.getResultCode();
                body = commandResult.getBody();
            } else if (result instanceof ResultCode) {
                code = (ResultCode)result;
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
        MessageContext context = null;
        switch (this.message.getMode()) {
            case PUSH:
                if (body != null) {
                    context = MessageContexts.push(head, code, body);
                }
                break;
            case REQUEST:
                context = MessageContexts.respond(this.message, code, body, this.message.getId());
                break;
        }
        if (context != null) {
            ProcessTracer allTracer = this.message.attributes().removeAttribute(NET_TRACE_ALL_ATTR_KEY);
            ProcessTracer writtenTracer = NET_TRACE_OUTPUT_WRITTEN_WATCHER.trace();
            if (allTracer != null) {
                context.willWriteFuture((f) -> {
                    allTracer.done();
                    writtenTracer.done();
                });
            }
            TunnelAides.responseMessage(this.tunnel, this.message, context);
        } else {
            if (code.getType() == ResultCodeType.ERROR) {
                LOGGER.error("code {}({}) {} error, close tunnel", code, code.getCode(), code.getType());
                this.tunnel.close();
            }
        }
    }

    private void fireExecuteStart() {
        for (MessageCommandListener listener : this.dispatcherContext.getCommandListener()) {
            try {
                listener.onExecuteStart(this);
            } catch (Throwable e) {
                LOGGER.error("on fireExecuteStart exception", e);
            }
        }
    }

    private void fireExecuteEnd(Throwable cause) {
        for (MessageCommandListener listener : this.dispatcherContext.getCommandListener()) {
            try {
                listener.onExecuteEnd(this, cause);
            } catch (Throwable e) {
                LOGGER.error("on fireExecuteEnd exception", e);
            }
        }
    }

    private void fireDone(Throwable cause) {
        for (MessageCommandListener listener : this.dispatcherContext.getCommandListener()) {
            try {
                listener.onDone(this, cause);
            } catch (Throwable e) {
                LOGGER.error("on fireDone( exception", e);
            }
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("message", this.message)
                .toString();
    }

}
