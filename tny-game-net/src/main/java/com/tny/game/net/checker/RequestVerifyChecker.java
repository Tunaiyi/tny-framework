package com.tny.game.net.checker;

import com.tny.game.net.dispatcher.Request;

/**
 * 消息校驗處理器
 *
 * @author KGTny
 */
public interface RequestVerifyChecker extends RequestChecker {

    /**
     * 加密
     *
     * @param request
     * @return
     */
    String generate(Request request);
}
