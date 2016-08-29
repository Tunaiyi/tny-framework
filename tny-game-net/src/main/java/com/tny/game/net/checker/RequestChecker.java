package com.tny.game.net.checker;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.dispatcher.Request;

/**
 * 消息校驗處理器
 *
 * @author KGTny
 */
public interface RequestChecker {

    /**
     * 是否检测 request
     *
     * @param request
     * @return 检测返回 true 否则返回 false
     */
    default boolean isCheck(Request request) {
        return true;
    }

    /**
     * 消息校驗
     *
     * @param request 请求对象
     * @return 是否通過校驗 通過校驗返回true 否則返回false
     */
    ResultCode match(Request request);

}
