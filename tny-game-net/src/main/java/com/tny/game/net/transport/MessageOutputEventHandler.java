package com.tny.game.net.transport;

/**
 * Session 输出消息处理器
 * Created by Kun Yang on 2017/2/23.
 */
public interface MessageOutputEventHandler<UID, T extends NetTunnel<UID>> {

    /**
     * 处理指定 tunnel 的输
     *
     * @param tunnel 处理指定 tunnel
     */
    void onOutput(T tunnel);

}
