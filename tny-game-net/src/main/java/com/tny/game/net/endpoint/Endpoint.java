package com.tny.game.net.endpoint;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.transport.*;

/**
 * 终端, 代表通选两端
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-09 19:21
 */
public interface Endpoint<UID> extends Netter<UID>, Sender<UID> {

    /*
     * @return 终端ID
     */
    long getId();

    /**
     * 发送过滤器
     *
     * @return 返回发送过滤器
     */
    MessageHandleFilter<UID> getSendFilter();

    /**
     * 接受过滤器
     *
     * @return 返回发送过滤器
     */
    MessageHandleFilter<UID> getReceiveFilter();

    /**
     * @return 获取会话属性
     */
    Attributes attributes();

    /**
     * 心跳
     */
    void heartbeat();

    /**
     * 设置发送过滤器
     *
     * @param filter 过滤器
     */
    void setSendFilter(MessageHandleFilter<UID> filter);

    /**
     * 设置发接受过滤器
     *
     * @param filter 过滤器
     */
    void setReceiveFilter(MessageHandleFilter<UID> filter);
}
