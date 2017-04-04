package com.tny.game.net.command;

import com.tny.game.net.command.listener.DispatchCommandListener;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.MessageFuture;
import com.tny.game.net.tunnel.Tunnel;

import java.util.Collection;

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
     * @param tunnel  通道
     */
    DispatchCommand<CommandResult> dispatch(Message message, MessageFuture<?> future, Tunnel<?> tunnel) throws DispatchException;

    /**
     * 添加请求派发错误监听器
     * <p>
     * <p>
     * 增加请求派发错误监听器<br>
     *
     * @param listener 添加的请求派发错误监听器
     */
    void addDispatcherRequestListener(DispatchCommandListener listener);

    /**
     * 添加请求派发错误监听器列表
     * <p>
     * <p>
     * 增加请求派发错误监听器<br>
     *
     * @param listeners 添加的请求派发错误监听器列表
     */
    void addDispatcherRequestListener(Collection<DispatchCommandListener> listeners);
    /**
     * 移除请求派发错误监听器
     * <p>
     * <p>
     * 移除指定的请求派发错误监听器<br>
     *
     * @param listener 移除的请求派发错误监听器
     */
    void removeDispatcherRequestListener(DispatchCommandListener listener);

    /**
     * 清除请求派发错误监听器
     * <p>
     * <p>
     * 清除所有请求派发错误监听器<br>
     */
    void clearDispatcherRequestListener();

}