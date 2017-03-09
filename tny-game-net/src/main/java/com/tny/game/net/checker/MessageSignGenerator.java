package com.tny.game.net.checker;

import com.tny.game.net.dispatcher.Request;

/**
 * 消息校驗码生成器
 *
 * @author KGTny
 */
public interface MessageSignGenerator {

    /**
     * 加密
     *
     * @param request
     * @return
     */
    String generate(Request request);

}
