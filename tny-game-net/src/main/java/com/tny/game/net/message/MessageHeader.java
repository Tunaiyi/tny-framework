package com.tny.game.net.message;

import com.tny.game.common.utils.ReferenceType;

/**
 * Created by Kun Yang on 2018/7/17.
 */
public interface MessageHeader extends Protocol {

    /**
     * @return 返回请求Id
     */
    long getId();

    /**
     * @return 消息响应码
     */
    int getCode();

    /**
     * 获取请求时间
     *
     * @return 返回请求时间
     */
    long getTime();

    /**
     * @return 响应消息 -1 为无
     */
    long getToMessage();

    /**
     * @return 获取消息模式
     */
    MessageMode getMode();

    /**
     * 判断消息是否有头部
     *
     * @return 如果有返回 true, 否则返回 false
     */
    boolean isHasAttachment();

    /**
     * @return 获取消息头
     */
    <T> T getAttachment(Class<T> clazz);

    /**
     * @return 获取消息头
     */
    <T> T getAttachment(ReferenceType<T> clazz);

}