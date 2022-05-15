package com.tny.game.net.command.dispatcher;

import com.google.common.base.MoreObjects;
import com.tny.game.common.result.*;
import com.tny.game.common.runtime.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

import static com.tny.game.common.runtime.TrackPrintOption.*;
import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public abstract class MessageCommand implements Command {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageCommand.class);

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private boolean start = false;

    protected MessageCommandContext commandContext;

    protected NetTunnel<Object> tunnel;

    private RelayTunnel<Object> relayTunnel;

    private final boolean relay;

    protected NetMessage message;

    protected RpcForwardHeader forward;

    protected MessageDispatcherContext dispatcherContext;

    protected EndpointKeeperManager endpointKeeperManager;

    public String getAppType() {
        return this.dispatcherContext.getAppContext().getAppType();
    }

    public String getScopeType() {
        return this.dispatcherContext.getAppContext().getScopeType();
    }

    protected MessageCommand(MessageCommandContext commandContext, NetTunnel<?> tunnel, Message message, MessageDispatcherContext dispatcherContext,
            EndpointKeeperManager endpointKeeperManager, boolean relay) {
        this.tunnel = as(tunnel);
        this.message = as(message);
        this.dispatcherContext = dispatcherContext;
        this.endpointKeeperManager = endpointKeeperManager;
        this.commandContext = commandContext;
        this.forward = message.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        this.relay = relay;
        if (relay) {
            if (tunnel instanceof RelayTunnel) {
                relayTunnel = as(tunnel);
            } else {
                throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION, "not relay tunnel");
            }
        }
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
     * command 执行
     */
    @Override
    public void execute() {
        try (ProcessTracer ignored = ProcessWatcher.of(this.getName(), CLOSE, (w) -> w.schedule(15, TimeUnit.SECONDS)).trace()) {
            Throwable cause = null;
            MessageHead head = this.message.getHead();
            ControllerContext.setCurrent(head.getProtocolId());
            MessageCommandPromise promise = this.commandContext.getPromise();
            try {
                if (isDone()) {
                    return;
                }
                this.fireExecuteStart();
                if (!this.start) {
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
            } catch (Throwable e) {
                cause = e;
                promise.setResult(e);
            } finally {
                if (this.isDone()) {
                    this.handleResult();
                }
                this.fireExecuteEnd(cause);
            }
        }
    }

    /**
     * 执行 invoke
     */
    protected abstract void invoke() throws Exception;

    private void invokeDone(Throwable cause) {
        doInvokeDone(cause);
        this.fireDone(cause);
    }

    protected abstract void doInvokeDone(Throwable cause);

    /**
     * 通过Throwable获取返回结果
     *
     * @param e 异常
     * @return 返回消息
     */
    private RpcResult<?> exceptionResult(Throwable e) {
        if (e instanceof CommandException) {
            CommandException dex = (CommandException)e;
            DISPATCHER_LOG.error(dex.getMessage(), dex);
            return RpcResults.fail(dex.getResultCode(), dex.getBody());
        } else if (e instanceof InvocationTargetException) {
            return this.exceptionResult(((InvocationTargetException)e).getTargetException());
        } else if (e instanceof ExecutionException) {
            return this.exceptionResult(e.getCause());
        } else {
            DISPATCHER_LOG.error("Controller [{}] exception", getName(), e);
            return RpcResults.fail(NetResultCode.SERVER_EXECUTE_EXCEPTION);
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
        boolean voidable = false;
        Object body = null;
        if (promise.isSuccess()) {
            Object result = promise.getResult();
            voidable = promise.isVoidable();
            if (result instanceof RpcResult) {
                RpcResult<?> commandResult = as(result);
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
            RpcResult<?> commandResult = exceptionResult(promise.getCause());
            code = commandResult.getResultCode();
            body = commandResult.getBody();
            this.invokeDone(cause);
        }
        MessageContext context = null;
        RpcForwardHeader messageForwardHeader = message.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        RpcOriginalMessageIdHeader messageIdHeader = message.getHeader(MessageHeaderConstants.RPC_ORIGINAL_MESSAGE_ID);
        switch (this.message.getMode()) {
            case PUSH:
                if (body != null) {
                    context = MessageContexts.push(head, code, body)
                            .withHeader(createBackForwardHeader(messageForwardHeader));
                }
                break;
            case REQUEST:
                if (!relay || !voidable) { // 如果转发并且返回值为 void, 表名由relay进行返回
                    long toMessage = this.message.getId();
                    if (messageIdHeader != null) {
                        toMessage = messageIdHeader.getMessageId();
                    }
                    context = MessageContexts.respond(this.message, code, body, toMessage)
                            .withHeader(createBackForwardHeader(messageForwardHeader));
                }
                break;
        }
        if (context != null) {
            TunnelAide.responseMessage(this.tunnel, code, context);
        }
        if (relay && code.isSuccess()) {
            this.relayTunnel.relay(message, false);
        }
    }

    private RpcForwardHeader createBackForwardHeader(RpcForwardHeader messageForwardHeader) {
        if (messageForwardHeader != null) {
            return RpcForwardHeaderBuilder.newBuilder()
                    .setFrom(messageForwardHeader.getTo())
                    .setSender(messageForwardHeader.getReceiver())
                    .setTo(messageForwardHeader.getFrom())
                    .setReceiver(messageForwardHeader.getSender())
                    .build();
        }
        return null;
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
        return MoreObjects.toStringHelper(this).add("message", this.message).toString();
    }

}
