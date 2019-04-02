package com.tny.game.net.message;

import com.tny.game.common.utils.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface MessageSubject extends Protocol {

    /**
     * @return 获取结果码
     */
    int getCode();

    /**
     * @return 获取消息模式
     */
    MessageMode getMode();

    /**
     * @return 响应消息
     */
    long getToMessage();

    /**
     * @return 是否存在消息
     */
    boolean existBody();

    /**
     * @return 获取消息体
     */
    <T> T getBody(Class<T> clazz);

    /**
     * @return 获取消息体
     */
    <T> T getBody(ReferenceType<T> clazz);

    /**
     * 判断消息是否有头部
     *
     * @return 如果有返回 true, 否则返回 false
     */
    boolean existTail();

    /**
     * @return 获取消息体
     */
    <T> T getTail(Class<T> clazz);

    /**
     * @return 获取消息体
     */
    <T> T getTail(ReferenceType<T> clazz);


}
