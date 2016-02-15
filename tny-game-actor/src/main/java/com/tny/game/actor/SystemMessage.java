package com.tny.game.actor;

import com.tny.game.actor.system.SystemMessageType;

/**
 * 系统消息
 */
public interface SystemMessage {

    /**
     * @return 返回系统消息类型
     */
    SystemMessageType messageType();

}
