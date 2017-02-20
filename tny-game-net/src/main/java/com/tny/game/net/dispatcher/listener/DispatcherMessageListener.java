package com.tny.game.net.dispatcher.listener;

/**
 * @author KGTny
 * @ClassName: DispatcherRequestListener
 * @Description: 请求派发监听器
 * @date 2011-11-18 上午10:59:39
 * <p>
 * <p>
 * <br>
 */
public interface DispatcherMessageListener {

    /**
     * 执行 <br>
     *
     * @param event
     */
    void execute(DispatcherMessageEvent event);

    /**
     * 执行业务完成 <br>
     *
     * @param event
     */
    void finish(DispatcherMessageEvent event);

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
    void executeException(DispatcherMessageErrorEvent event);

}
