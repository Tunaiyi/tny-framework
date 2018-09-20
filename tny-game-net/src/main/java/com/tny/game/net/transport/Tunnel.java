package com.tny.game.net.transport;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.transport.message.MessageMode;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface Tunnel<UID> extends Communicator<UID> {

    /**
     * @return 通道 Id
     */
    long getId();

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


}
