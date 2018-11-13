package com.tny.game.net.message;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.utils.ReferenceType;

import java.io.Serializable;

/**
 * 消息接口
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018/8/31 下午10:23
 */

public interface Message<UID> extends Serializable, Protocol {

    /**
     * @return 获取消息 ID
     */
    default long getId() {
        return this.getHeader().getId();
    }

    /**
     * @return 用户ID 无用户ID返回-1
     */
    UID getUserID();

    /**
     * @return 是否已经登录
     */
    boolean isLogin();

    /**
     * @return 获取消息头
     */
    MessageHeader getHeader();

    /**
     * @return 獲取用户类型
     */
    String getUserType();

    /**
     * @return 获取请求属性
     */
    Attributes attributes();

    /**
     * @return 获取消息模式
     */
    default MessageMode getMode() {
        return getHeader().getMode();
    }

    /**
     * @return 获取协议码
     */
    default int getProtocol() {
        return this.getHeader().getProtocolNumber();
    }

    /**
     * @return 获取结果码
     */
    default int getCode() {
        return this.getHeader().getCode();
    }

    @Override
    default int getProtocolNumber() {
        return getHeader().getProtocolNumber();
    }

    /**
     * @return 获取消息体
     */
    <T> T getBody(Class<T> clazz);

    /**
     * @return 获取消息体
     */
    <T> T getBody(ReferenceType<T> clazz);

}
