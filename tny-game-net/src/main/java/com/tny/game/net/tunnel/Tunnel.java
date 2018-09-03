package com.tny.game.net.tunnel;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.session.*;

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
     * @return 最后读取时间
     */
    long getLastReadAt();

    /**
     * @return 最后写时间
     */
    long getLastWriteAt();

    /**
     * @return 属性对象
     */
    Attributes attributes();

    /**
     * @return 会话对象
     */
    Session<UID> getSession();

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
    default void receiveExcludes(MessageMode... modes) {
        receiveExcludes(Arrays.asList(modes));
    }

    /**
     * 设置接收排除
     *
     * @param modes 接收排除模型
     */
    void receiveExcludes(Collection<MessageMode> modes);

    /**
     * 发送接收排除
     *
     * @param modes 发送排除模型
     */
    default void sendExcludes(MessageMode... modes) {
        sendExcludes(Arrays.asList(modes));
    }

    /**
     * 发送接收排除
     *
     * @param modes 发送排除模型
     */
    void sendExcludes(Collection<MessageMode> modes);

    /**
     * @return 获取接收排除列表
     */
    boolean isReceiveExclude(MessageMode mode);

    /**
     * @return 发送接收排除列表
     */
    boolean isSendExclude(MessageMode mode);

}
