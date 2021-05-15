package com.tny.game.net.command.dispatcher;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.Collection;

/**
 * 请求派发器
 *
 * @author KGTny
 */
@UnitInterface
public interface MessageDispatcher {

    /**
     * 派发消息事件
     * <p>
     * <p>
     * 派发消息事件到相对应的Controller<br>
     *
     * @param tunnel  通道
     * @param message 消息
     * @return
     */
    Command dispatch(NetTunnel<?> tunnel, Message message) throws CommandException;

    /**
     * 添加请求派发错误监听器
     * <p>
     * <p>
     * 增加请求派发错误监听器<br>
     *
     * @param listener 添加的请求派发错误监听器
     */
    void addCommandListener(MessageCommandListener listener);

    /**
     * 添加请求派发错误监听器列表
     * <p>
     * <p>
     * 增加请求派发错误监听器<br>
     *
     * @param listeners 添加的请求派发错误监听器列表
     */
    void addCommandListener(Collection<MessageCommandListener> listeners);

    /**
     * 移除请求派发错误监听器
     * <p>
     * <p>
     * 移除指定的请求派发错误监听器<br>
     *
     * @param listener 移除的请求派发错误监听器
     */
    void removeCommandListener(MessageCommandListener listener);

    /**
     * 清除请求派发错误监听器
     * <p>
     * <p>
     * 清除所有请求派发错误监听器<br>
     */
    void clearCommandListeners();

}