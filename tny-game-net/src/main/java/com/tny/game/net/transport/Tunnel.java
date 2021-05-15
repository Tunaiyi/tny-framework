package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;

import java.net.InetSocketAddress;

/**
 * 通道
 * Created by Kun Yang on 2017/3/26.
 */
public interface Tunnel<UID> extends Netter<UID> {

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
     * @return 是否可用
     */
    boolean isAvailable();

    /**
     * @return 是否已经开启
     */
    boolean isActive();

    /**
     * @return 获取 Tunnel 状态
     */
    TunnelStatus getStatus();

    /**
     * @return 返回远程地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * @return 返回本地地址
     */
    InetSocketAddress getLocalAddress();

    /**
     * @return 获取绑定中断
     */
    Endpoint<UID> getEndpoint();

}
