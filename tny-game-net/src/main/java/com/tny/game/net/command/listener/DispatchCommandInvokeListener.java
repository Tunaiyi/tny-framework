package com.tny.game.net.command.listener;

import com.tny.game.net.command.DispatchContext;

/**
 * @author KGTny
 * @ClassName: DispatcherRequestListener
 * @Description: 请求派发监听器
 * @date 2011-11-18 上午10:59:39
 * <p>
 * <p>
 * <br>
 */
public interface DispatchCommandInvokeListener extends DispatchCommandListener {

    /**
     * 将要调用 <br>
     *
     * @param context
     */
    default void willInvoke(DispatchContext context) {
    }

    /**
     * 开始调用 <br>
     *
     * @param context
     */
    default void invoke(DispatchContext context) {
    }

    /**
     * 调用业务成功后 <br>
     *
     * @param context
     */
    default void invoked(DispatchContext context) {
    }

    /**
     * 调用业务完成 无论成功失败 <br>
     *
     * @param context
     */
    default void invokeFinish(DispatchContext context, Throwable cause) {
    }

    /**
     * 调用业务错误 <br>
     *
     * @param context
     */
    default void invokeException(DispatchContext context, Throwable cause) {
    }

}
