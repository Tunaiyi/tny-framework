package com.tny.game.net.command.listener;

import com.tny.game.common.unit.annotation.UnitInterface;
import com.tny.game.net.command.dispatcher.DispatchContext;

/**
 * @author KGTny
 * @ClassName: DispatcherRequestListener
 * @Description: 请求派发监听器
 * @date 2011-11-18 上午10:59:39
 * <p>
 * <p>
 * <br>
 */
@UnitInterface
public interface DispatchCommandListener {

    /**
     * 每次 Command 执行开始<br>
     *
     * @param context 分发上下文
     */
    default void onExecuteStart(DispatchContext context) {
    }

    /**
     * 每次 Command 执行结束<br>
     *
     * @param context 分发上下文
     * @param cause   失败异常, 成功为 null
     */
    default void onExecuteEnd(DispatchContext context, Throwable cause) {
    }

    /**
     * 执行Command异常完成 <br>
     *
     * @param context 分发上下文
     * @param cause   失败异常
     */
    default void onDoneError(DispatchContext context, Throwable cause) {
    }

    /**
     * Command任务成功完成  <br>
     *
     * @param context 分发上下文
     */
    default void onDoneSuccess(DispatchContext context) {
    }

    /**
     * 执行Command任务完成  <br>
     *
     * @param context 分发上下文
     * @param cause   失败异常, 成功为 null
     */
    default void onDone(DispatchContext context, Throwable cause) {
    }


}
