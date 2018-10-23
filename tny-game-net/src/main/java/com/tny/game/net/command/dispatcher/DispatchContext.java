package com.tny.game.net.command.dispatcher;

import com.tny.game.common.concurrent.Waiter;
import com.tny.game.common.worker.command.Command;
import com.tny.game.net.base.NetResultCode;
import com.tny.game.net.command.listener.DispatchCommandListener;
import com.tny.game.net.exception.CommandTimeoutException;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 2017/5/27.
 */
public abstract class DispatchContext extends InvokeContext implements Command {

    public static final Logger LOGGER = LoggerFactory.getLogger(DispatchContext.class);

    protected NetTunnel<Object> tunnel;

    protected NetMessage<Object> message;

    protected MessageDispatcherContext context;

    private Waiter<Object> waiter;

    private long timeout;

    private boolean done;

    private String name;

    protected DispatchContext(MessageDispatcherContext context, MethodControllerHolder methodHolder, NetTunnel<?> tunnel, Message<?> message) {
        super(methodHolder);
        this.tunnel = as(tunnel);
        this.message = as(message);
        this.context = context;
    }

    @Override
    public String getName() {
        if (this.name != null)
            return this.name;
        MethodControllerHolder controller = this.controller;
        if (controller == null) {
            MessageHeader header = message.getHeader();
            return this.name = String.valueOf(header.getId());
        } else {
            return this.name = controller.getControllerClass() + "." + controller.getName();
        }
    }

    @Override
    public String getAppType() {
        return context.getAppConfiguration().getAppType();
    }

    @Override
    public String getScopeType() {
        return context.getAppConfiguration().getScopeType();
    }

    @Override
    public boolean isDone() {
        return isIntercept() || done;
    }

    public <T> Tunnel<T> getTunnel() {
        return as(tunnel);
    }

    public <T> Message<T> getMessage() {
        return as(message);
    }

    protected boolean isWaiting() {
        return this.waiter != null;
    }

    protected void done() {
        this.done = true;
    }

    /**
     * 设置等待器
     *
     * @param waiter 等待器
     */
    protected void waitingFor(Waiter<Object> waiter) {
        this.waiter = waiter;
        this.timeout = System.currentTimeMillis() + 5000; // 超时
        this.setResult((Object) null);
    }

    /**
     * 检测等待器
     *
     * @throws Throwable 等待器异常
     */
    protected void checkWaiter() throws Throwable {
        if (this.waiter != null) { // 检测是否
            if (this.waiter.isDone()) {
                this.onWaiterDone(this.waiter);
            } else {
                if (System.currentTimeMillis() > timeout) {
                    this.waiter = null;
                    this.setResult((Object) null);
                    throw new CommandTimeoutException(NetResultCode.EXECUTE_TIMEOUT, format("执行 {} 超时", this.controller.getName()));
                }
            }
        }
    }

    protected abstract void onWaiterDone(Waiter<Object> waiter) throws Throwable;

    protected void fireExecuteStart() {
        for (DispatchCommandListener listener : this.context.getDispatchListeners()) {
            try {
                listener.onExecuteStart(this);
            } catch (Throwable e) {
                LOGGER.error("on fireExecuteStart exception", e);
            }
        }
    }

    protected void fireExecuteEnd(Throwable cause) {
        for (DispatchCommandListener listener : this.context.getDispatchListeners()) {
            try {
                listener.onExecuteEnd(this, cause);
            } catch (Throwable e) {
                LOGGER.error("on fireExecuteEnd exception", e);
            }
        }
    }

    protected void fireDoneError(Throwable cause) {
        for (DispatchCommandListener listener : this.context.getDispatchListeners()) {
            try {
                listener.onDoneError(this, cause);
            } catch (Throwable e) {
                LOGGER.error("on fireDoneError exception", e);
            }
        }
    }

    protected void fireDoneSuccess() {
        for (DispatchCommandListener listener : this.context.getDispatchListeners()) {
            try {
                listener.onDoneSuccess(this);
            } catch (Throwable e) {
                LOGGER.error("on fireDoneSuccess exception", e);
            }
        }
    }

    protected void fireDone(Throwable cause) {
        for (DispatchCommandListener listener : this.context.getDispatchListeners()) {
            try {
                listener.onDone(this, cause);
            } catch (Throwable e) {
                LOGGER.error("on fireDone( exception", e);
            }
        }
    }

}

