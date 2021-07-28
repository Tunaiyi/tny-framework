package com.tny.game.net.command.listener;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.command.dispatcher.*;

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
public interface MessageCommandListener {

    /**
     * 每次 Command 执行开始<br>
     *
     * @param context 分发上下文
     */
    default void onExecuteStart(MessageCommand<? extends MessageCommandContext> context) {
    }

    /**
     * 每次 Command 执行结束<br>
     *
     * @param context 分发上下文
     * @param cause   失败异常, 成功为 null
     */
    default void onExecuteEnd(MessageCommand<? extends MessageCommandContext> context, Throwable cause) {
    }

    /**
     * 执行Command任务完成  <br>
     *
     * @param context 分发上下文
     * @param cause   失败异常, 成功为 null
     */
    default void onDone(MessageCommand<? extends MessageCommandContext> context, Throwable cause) {
    }

}
