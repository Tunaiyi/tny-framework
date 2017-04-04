package com.tny.game.net.command.listener;

/**
 * @author KGTny
 * @ClassName: DispatcherRequestListener
 * @Description: 请求派发监听器
 * @date 2011-11-18 上午10:59:39
 * <p>
 * <p>
 * <br>
 */
public interface DispatchCommandListener {

    /**
     * 执行 <br>
     *
     * @param event
     */
    default void receive(DispatchCommandExecuteEvent event) {
    }

    /**
     * 执行 <br>
     *
     * @param event
     */
    default void execute(DispatchCommandExecuteEvent event) {
    }

    /**
     * 执行业务完成 <br>
     *
     * @param event
     */
    default void executeFinish(DispatchCommandExecuteEvent event) {
    }

    /**
     * 消息处理完成 <br>
     *
     * @param event
     */
    default void dispatchFinish(DispatchCommandEvent event) {
    }

    /**
     * 执行错误 <br>
     *
     * @param event
     */
    default void executeException(DispatchCommandExceptionEvent event) {
    }

}
