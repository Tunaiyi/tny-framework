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

public interface Message<UID> extends Serializable {

    /**
     * @return 获取消息 ID
     */
    default int getId() {
        return this.getHeader().getId();
    }

    /**
     * @return 所属sesssion
     */
    long getSessionID();

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
     * @return 獲取所属用户组
     */
    String getUserGroup();

    /**
     * @return 获取协议码
     */
    default int getProtocol() {
        return this.getHeader().getProtocol();
    }

    /**
     * @return 获取结果码
     */
    default int getCode() {
        return this.getHeader().getCode();
    }

    /**
     * @return 获取消息体
     */
    <T> T getBody(Class<T> clazz);

    /**
     * @return 获取消息体
     */
    <T> T getBody(ReferenceType<T> clazz);

    /**
     * @return 获取请求属性
     */
    Attributes attributes();

    /**
     * @return 获取消息模式
     */
    MessageMode getMode();

}
