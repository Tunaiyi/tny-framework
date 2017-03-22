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
public interface MessageDispatcherListener {

    /**
     * 执行 <br>
     *
     * @param event
     */
    void receive(ExecuteMessageEvent event);

    /**
     * 执行 <br>
     *
     * @param event
     */
    void execute(ExecuteMessageEvent event);

    /**
     * 执行业务完成 <br>
     *
     * @param event
     */
    void executeFinish(ExecuteMessageEvent event);

    /**
     * 消息处理完成 <br>
     *
     * @param event
     */
    void dispatchFinish(DispatchMessageEvent event);

    /**
     * 执行错误 <br>
     *
     * @param event
     */
    void executeDispatchException(DispatchExceptionEvent event);

    /**
     * 执行错误 <br>
     *
     * @param event
     */
    void executeException(DispatchMessageErrorEvent event);

}
