package com.tny.game.net.checker;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Message;

/**
 * 消息校驗處理器
 *
 * @author KGTny
 */
public interface MessageChecker {

    /**
     * 是否检测 request
     *
     * @param message
     * @return 检测返回 true 否则返回 false
     */
    default boolean isCheck(Message message) {
        return true;
    }

    /**
     * 消息校驗
     *
     * @param message 请求对象
     * @return 是否通過校驗 通過校驗返回true 否則返回false
     */
    ResultCode match(Message message);

}
