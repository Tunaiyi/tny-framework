package com.tny.game.net.command.dispatcher;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.Executor;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * @author KGTny
 * @ClassName: ControllerInfo
 * @date 2011-10-26 下午4:22:47
 * <p>
 * 控制器信息
 * <p>
 * <br>
 */
public class RpcHandleContext {

    private static final ThreadLocal<RpcHandleContext> CONTEXT = new ThreadLocal<>();

    private MessageCommand command;

    private Thread thread;

    private RpcHandleContext() {
    }

    private RpcHandleContext(Thread thread) {
        this.thread = thread;
    }

    /**
     * 获取当前线程正在执行的控制信息 <br>
     *
     * @return
     */
    public static RpcHandleContext current() {
        RpcHandleContext info = CONTEXT.get();
        if (info == null) {
            info = new RpcHandleContext();
            CONTEXT.set(info);
        }
        return info;
    }

    /**
     * @return 获取当前线程正在执行的终端
     */
    public static <U> Endpoint<U> currentEndpoint() {
        return current().getEndpoint();
    }

    /**
     * @return 获取当前线程正在执行的通道
     */
    public static <U> Tunnel<U> currentTunnel() {
        return current().getTunnel();
    }

    /**
     * @return 获取当前线程正在处理的消息
     */
    public static Message currentMessage() {
        return current().getMessage();
    }

    static void setCurrent(MessageCommand command) {
        RpcHandleContext info = CONTEXT.get();
        if (info == null) {
            info = new RpcHandleContext();
            CONTEXT.set(info);
        }
        info.command = command;
    }

    static void clean() {
        RpcHandleContext info = CONTEXT.get();
        if (info != null) {
            info.command = null;
        }
    }

    public Thread getThread() {
        return this.thread;
    }

    /**
     * @return 获取消息
     */
    public Message getMessage() {
        return this.command.getMessage();
    }

    /**
     * @return 获取终端
     */
    public <U> Endpoint<U> getEndpoint() {
        return as(this.command.getEndpoint());
    }

    /**
     * @return 获取通道
     */
    public <U> Tunnel<U> getTunnel() {
        return as(this.command.getTunnel());
    }

    /**
     * @return 当前用户执行器
     */
    public Executor getExecutor() {
        return this.command.getEndpoint();
    }

    @Override
    public String toString() {
        return "ControllerInfo [getProtocol()=" + this.command + "]";
    }

}
