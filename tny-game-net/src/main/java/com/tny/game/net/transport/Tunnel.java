package com.tny.game.net.transport;

import com.tny.game.common.context.Attributes;

import java.net.InetSocketAddress;

/**
 * 通道
 * Created by Kun Yang on 2017/3/26.
 */
public interface Tunnel<UID> extends Netter<UID>, Transport<UID>, Sender<UID>, Receiver<UID> {

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
     * @return 属性对象
     */
    Attributes attributes();

    /**
     * @return 是否可用
     */
    boolean isAvailable();

    /**
     * @return 是否已经开启
     */
    boolean isAlive();

    /**
     * @return 获取 Tunnel 状态
     */
    TunnelState getState();

    /**
     * @return 返回远程地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * @return 返回本地地址
     */
    InetSocketAddress getLocalAddress();

}
