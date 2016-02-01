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
public interface DispatcherRequestListener {

    /**
     * 执行 <br>
     *
     * @param event
     */
    public void execute(DispatcherRequestEvent event);

    /**
     * 执行业务完成 <br>
     *
     * @param event
     */
    public void finish(DispatcherRequestEvent event);

    /**
     * 执行错误 <br>
     *
     * @param event
     */
    public void executeDispatchException(DispatchExceptionEvent event);

    /**
     * 执行错误 <br>
     *
     * @param event
     */
    public void executeException(DispatcherRequestErrorEvent event);

}
