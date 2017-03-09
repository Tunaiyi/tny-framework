package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Message;
import com.tny.game.net.dispatcher.exception.DispatchException;
import com.tny.game.net.dispatcher.listener.MessageDispatcherListener;

/**
 * 请求派发器
 *
 * @author KGTny
 * @ClassName: ControllerDispatcher
 * @Description:
 * @date 2011-9-13 下午1:23:19
 * <p>
 * <p>
 * 将请求派发到相对应的Controller上<br>
 */
public interface MessageDispatcher {

    /**
     * 派发请求
     * <p>
     * <p>
     * 派发请求到相对应的Controller<br>
     *
     * @param message 请求
     * @param session 通道
     */
    DispatcherCommand<CommandResult> dispatch(Message message, NetSession<?> session) throws DispatchException;

    /**
     * 添加请求派发错误监听器
     * <p>
     * <p>
     * 增加请求派发错误监听器<br>
     *
     * @param listener 添加的请求派发错误监听器
     */
    void addDispatcherRequestListener(MessageDispatcherListener listener);

    /**
     * 移除请求派发错误监听器
     * <p>
     * <p>
     * 移除指定的请求派发错误监听器<br>
     *
     * @param listener 移除的请求派发错误监听器
     */
    void removeDispatcherRequestListener(MessageDispatcherListener listener);

    /**
     * 清除请求派发错误监听器
     * <p>
     * <p>
     * 清除所有请求派发错误监听器<br>
     */
    void clearDispatcherRequestListener();

}