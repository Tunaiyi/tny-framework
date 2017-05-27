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
public interface DispatchCommandExecuteListener extends DispatchCommandListener {

    /**
     * 开始消息处理 <br>
     *
     * @param context
     */
    default void execute(DispatchContext context) {
    }

    /**
     * 开始成功处理 <br>
     *
     * @param context
     */
    default void executed(DispatchContext context) {
    }

    /**
     * 执行错误 <br>
     *
     * @param context
     */
    default void executeException(DispatchContext context, Throwable cause) {
    }

    /**
     * 消息处理完成 <br>
     *
     * @param context
     */
    default void executeFinish(DispatchContext context, Throwable cause) {
    }


}
