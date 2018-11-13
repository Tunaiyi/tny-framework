package com.tny.game.net.message;

import com.tny.game.common.unit.annotation.UnitInterface;
import com.tny.game.net.transport.*;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
@UnitInterface
public interface MessageFactory<UID> {

    NetMessage<UID> create(long id, MessageContext<UID> context, Certificate<UID> certificate);

    NetMessage<UID> create(NetMessageHeader header, Object body);

}
