package com.tny.game.net.message;

import com.tny.game.common.unit.annotation.*;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
@UnitInterface
public interface MessageFactory<UID> {

    NetMessage create(long id, MessageContent subject);

    NetMessage create(NetMessageHead head, Object body);

}
