package com.tny.game.net.message;

/**
 * Created by Kun Yang on 2018/7/17.
 */
public interface MessageHeader extends Protocol {

    /**
     * @return 返回请求Id
     */
    int getID();

    /**
     * @return 消息响应码
     */
    int getCode();

    /**
     * 获取请求时间
     * <p>
     * <p>
     * 获取请求时间 <br>
     *
     * @return 返回请求时间
     */
    long getTime();

    /**
     * @return 响应消息 -1 为无
     */
    int getToMessage();

    /**
     * 判断消息是否有头部
     *
     * @return 如果有返回 true, 否则返回 false
     */
    boolean isHasHead();

    /**
     * @return 获取消息头
     */
    <T> T getHead(Class<T> clazz);

    /**
     * @return 获取消息头
     */
    <T> T getHead(ReferenceType<T> clazz);

}
