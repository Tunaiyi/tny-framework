package com.tny.game.net.message;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
@UnitInterface
public interface MessageFactory {

    NetMessage create(long id, MessageContent subject);

    NetMessage create(NetMessageHead head, Object body);

}
