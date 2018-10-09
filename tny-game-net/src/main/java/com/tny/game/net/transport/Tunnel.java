package com.tny.game.net.transport;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.exception.NetException;
import com.tny.game.net.transport.message.MessageMode;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * 通道
 * Created by Kun Yang on 2017/3/26.
 */
public interface Tunnel<UID> extends Communicator<UID> {

    /**
     * @return 通道 Id
     */
    long getId();

    /**
     * @return 终端模式
     */
    TunnelMode getMode();

    /**
     * @return 属性对象
     */
    Attributes attributes();

    /**
     * @return 返回远程地址
     */
    InetSocketAddress remoteAddress();

    /**
     * @return 返回本地地址
     */
    InetSocketAddress localAddress();

    /**
     * 重新发送消息
     *
     * @param from 指定开始 Id 如果 Id < 0, 表示从缓存第一个开始
     * @param to   指定结束 Id 如果 Id < 0, 表示到缓存最后一个结束
     * @throws NetException
     */
    void resend(long from, long to) throws NetException;

    /**
     * 设置接收排除
     *
     * @param modes 接收排除模型
     */
    default void excludeReceiveModes(MessageMode... modes) {
        excludeReceiveModes(Arrays.asList(modes));
    }

    /**
     * 设置接收排除
     *
     * @param modes 接收排除模型
     */
    void excludeReceiveModes(Collection<MessageMode> modes);

    /**
     * 发送接收排除
     *
     * @param modes 发送排除模型
     */
    default void excludeSendModes(MessageMode... modes) {
        excludeSendModes(Arrays.asList(modes));
    }

    /**
     * 发送接收排除
     *
     * @param modes 发送排除模型
     */
    void excludeSendModes(Collection<MessageMode> modes);

    /**
     * @return 获取接收排除列表
     */
    boolean isExcludeReceiveMode(MessageMode mode);

    /**
     * @return 发送接收排除列表
     */
    boolean isExcludeSendMode(MessageMode mode);

    /**
     * 获取绑定会话
     *
     * @return 获取邦定回话
     */
    boolean isBind();

    /**
     * 获取绑定会话
     *
     * @return 获取邦定回话
     */
    Optional<NetSession<UID>> getBindSession();

}
