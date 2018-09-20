package com.tny.game.net.transport;

import com.tny.game.net.exception.ValidatorFailException;

public interface NetSessionKeeper<UID> extends SessionKeeper<UID> {

    /**
     * <p>
     * <p>
     * 添加指定的session<br>
     *
     * @param tunnel 注册tunnel
     * @throws ValidatorFailException 认证异常
     */
    boolean online(NetTunnel<UID> tunnel) throws ValidatorFailException;

    /**
     * @return 会话工厂
     */
    SessionFactory<UID> getSessionFactory();

}
