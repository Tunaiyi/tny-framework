package com.tny.game.net.command.checker;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.command.dispatcher.ControllerHolder;
import com.tny.game.net.message.Message;

/**
 * 消息校驗處理器
 *
 * @author KGTny
 */
public interface ControllerChecker<UID, O> {

    /**
     * 消息校驗
     *
     * @param message 请求对象
     * @return 是否通過校驗 通過校驗返回true 否則返回false
     */
    ResultCode check(Tunnel<UID> tunnel, Message<UID> message, ControllerHolder holder, O attribute);

    /**
     * @return 属性类型
     */
    default Class<?> getAttributesClass() {
        return Object.class;
    }

    // /**
    //  * 是否检测 request
    //  *
    //  * @param message
    //  * @return 检测返回 true 否则返回 false
    //  */
    // default boolean isCheck(Message message, ControllerHolder holder, Config config) {
    //     return true;
    // }

}
