package com.tny.game.net.message;

import com.tny.game.net.transport.Certificate;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public interface MessageFactory<UID> {

    NetMessage<UID> create(long id, MessageSubject subject, Object attachment, Certificate<UID> certificate);

    NetMessage<UID> create(NetMessageHeader header, Object body);

}
