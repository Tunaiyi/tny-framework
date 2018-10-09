package com.tny.game.net.transport.message;

import com.tny.game.net.transport.Certificate;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public interface MessageFactory<UID> {

    NetMessage<UID> create(long id, MessageSubject subject, Certificate<UID> certificate);

}
