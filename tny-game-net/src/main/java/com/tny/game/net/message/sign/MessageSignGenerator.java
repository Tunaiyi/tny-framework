package com.tny.game.net.message.sign;

import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;

/**
 * 消息校驗码生成器
 *
 * @author KGTny
 */
public interface MessageSignGenerator<UID> {

    /**
     * 生成指定消息的校验码
     *
     * @param message 指定消息
     * @return 返回校验码
     */
    String generate(Tunnel<UID> tunnel, Message<UID> message);

}
