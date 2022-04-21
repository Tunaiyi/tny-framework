package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;

/**
 * 通道
 * Created by Kun Yang on 2017/3/26.
 */
public interface Tunnel<UID> extends Connection, Communicator<UID> {

    /**
     * @return 通道 Id
     */
    long getId();

    /**
     * @return 访问 Id
     */
    long getAccessId();

    /**
     * @return 终端模式
     */
    TunnelMode getMode();

    /**
     * @return 是否已经开启
     */
    boolean isOpen();

    /**
     * @return 获取 Tunnel 状态
     */
    TunnelStatus getStatus();

    /**
     * @return 获取绑定中断
     */
    Endpoint<UID> getEndpoint();

}
