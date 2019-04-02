package com.tny.game.net.message;

import com.tny.game.common.utils.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-18 10:50
 */
public interface MessageTailer {

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
