package com.tny.game.net.message;

import com.tny.game.net.transport.Certificate;

public interface NetMessage<UID> extends Message<UID> {

    /**
     * 设置授权
     *
     * @param certificate 授权
     * @return 返回 this
     */
    NetMessage<UID> update(Certificate<UID> certificate);

    // /**
    //  * 设置消息 Id
    //  *
    //  * @param messageID 消息 Id
    //  * @return 返回 this
    //  */
    // NetMessage<UID> setId(long messageID);

}
