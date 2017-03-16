package com.tny.game.net.base;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.session.Session;

public interface Message<UID> extends Protocol {

    /**
     * @return 返回请求Id
     */
    int getID();

    /**
     * @return 用户ID 无用户ID返回-1
     */
    UID getUserID();

    /**
     * @return 獲取所属用户组
     */
    String getUserGroup();

    // /**
    //  * @return 传输类型 请求|响应
    //  */
    // MessageType getMessage();

    /**
     * @return 消息响应码
     */
    int getCode();

    /**
     * @return 响应消息 -1 为无
     */
    int getToMessage();

    /**
     * @return 获取消息体
     */
    <T> T getBody(Class<T> clazz);

    /**
     * @return 獲取玩家會話對象
     */
    Session getSession();

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
     * @return 校验码
     */
    String getCheckCode();

    /**
     * @return 获取请求属性
     */
    Attributes attributes();

    /**
     * @return 获取HostName
     */
    String getHostName();

    /**
     * @return 获取消息模式
     */
    MessageMode getMode();
}
