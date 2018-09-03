package com.tny.game.net.session;

import com.tny.game.net.tunnel.NetTunnel;

/**
 * Session 输入消息处理器
 * Created by Kun Yang on 2017/2/23.
 */
public interface MessageInputEventHandler<UID, T extends NetTunnel<UID>> {

    /**
     * 处理指定 tunnel 的输入
     *
     * @param tunnel 处理指定 tunnel
     */
    void onInput(T tunnel);

}
