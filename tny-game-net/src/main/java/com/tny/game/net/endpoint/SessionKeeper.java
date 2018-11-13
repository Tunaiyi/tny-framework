package com.tny.game.net.endpoint;


import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.transport.NetTunnel;

import java.util.Optional;

/**
 * 会话管理器
 *
 * @param <UID>
 */
public interface SessionKeeper<UID> extends EndpointKeeper<UID, Session<UID>> {

    /**
     * 使指定userId的session下线
     *
     * @param userId 指定userId
     * @return 返回下线session
     */
    Session<UID> offline(UID userId);

    /**
     * 使所有session下线
     */
    void offlineAll();

    /**
     * <p>
     * <p>
     * 获取指定userId对应的Session <br>
     *
     * @param userId 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    boolean isOnline(UID userId);

    /**
     * <p>
     * <p>
     * 添加指定的session<br>
     *
     * @param tunnel 注册tunnel
     * @throws ValidatorFailException 认证异常
     */
    Optional<Session<UID>> online(NetTunnel<UID> tunnel) throws ValidatorFailException;

    /**
     * 计算在线人数
     */
    int onlineSize();

}
