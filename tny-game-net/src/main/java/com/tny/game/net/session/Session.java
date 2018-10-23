package com.tny.game.net.session;

import com.tny.game.net.transport.Endpoint;

/**
 * 用户会话对象 此对象从Socket链接便创建,保存用户链接后的属性对象,直到Socket断开连接
 *
 * @author KGTny
 */
public interface Session<UID> extends Endpoint<UID> {

    /**
     * @return 获取 session 状态
     */
    SessionState getState();

    /**
     * 是否已上线
     *
     * @return 连接返回true 否则返回false
     */
    boolean isOnline();

    /**
     * 是否已下线
     *
     * @return 连接返回true 否则返回false
     */
    boolean isOffline();

    /**
     * @return 获取下线时间
     */
    long getOfflineTime();

}
