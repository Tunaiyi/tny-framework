package com.tny.game.net.message;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.transport.*;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
@UnitInterface
public interface MessageFactory<UID> {

    NetMessage<UID> create(long id, MessageSubject subject, Certificate<UID> certificate);

    NetMessage<UID> create(NetMessageHead head, Object body, Object tail);

}
