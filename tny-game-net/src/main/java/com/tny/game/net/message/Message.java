package com.tny.game.net.message;

import com.tny.game.common.context.*;

import java.io.Serializable;

/**
 * 消息接口
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018/8/31 下午10:23
 */

public interface Message<UID> extends Serializable, MessageSubject {

    /**
     * @return 获取消息 ID
     */
    default long getId() {
        return this.getHead().getId();
    }

    @Override
    default MessageMode getMode() {
        return getHead().getMode();
    }

    @Override
    default long getToMessage() {
        return this.getHead().getToMessage();
    }

    @Override
    default int getCode() {
        return this.getHead().getCode();
    }

    /**
     * @return 获取协议码
     */
    default int getProtocol() {
        return this.getHead().getProtocolNumber();
    }

    @Override
    default int getProtocolNumber() {
        return getHead().getProtocolNumber();
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
    MessageHead getHead();

    /**
     * @return 獲取用户类型
     */
    String getUserType();

    /**
     * @return 获取请求属性
     */
    Attributes attributes();


}
