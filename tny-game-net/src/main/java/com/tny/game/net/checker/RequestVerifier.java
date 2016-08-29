package com.tny.game.net.checker;

import com.tny.game.net.dispatcher.Request;

/**
 * 消息校驗處理器
 *
 * @author KGTny
 */
public interface RequestVerifier {

    /**
     * 加密
     *
     * @param request
     * @return
     */
    String generate(Request request);

}
