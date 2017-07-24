package com.tny.game.net.tunnel;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.session.Session;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface Tunnel<UID> extends Terminal<UID> {

    long getID();

    boolean isConnected();

    long getLatestActiveAt();

    Attributes attributes();

    Session<UID> getSession();

    String getHostName();

    /**
     * @return 是否登陆认证
     */
    boolean isLogin();

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
    Collection<MessageMode> getReceiveExcludes();

    /**
     * @return 发送接收排除列表
     */
    Collection<MessageMode> getSendExcludes();

}
