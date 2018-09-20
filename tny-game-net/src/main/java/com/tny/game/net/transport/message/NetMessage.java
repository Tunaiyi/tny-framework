package com.tny.game.net.transport.message;

import com.tny.game.net.transport.Certificate;

public interface NetMessage<UID> extends Message<UID> {

    /**
     * @param id 设置消息 Id
     */
    void setId(long id);

    /**
     * 设置授权
     *
     * @param certificate 授权
     */
    void update(Certificate<UID> certificate);
}
