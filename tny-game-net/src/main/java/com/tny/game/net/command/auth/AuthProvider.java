package com.tny.game.net.command.auth;

import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.LoginCertificate;
import com.tny.game.net.tunnel.Tunnel;

/**
 * Session登录验证器
 *
 * @author KGTny
 */
public interface AuthProvider<UID> {

    /**
     * 验证Session登录, 返回带有验证结果的Session对象
     *
     * @param message 与Session相对应的链接通道
     * @return 带有验证结果的Session对象
     * @throws DispatchException
     */
    LoginCertificate<UID> validate(Tunnel<UID> tunnel, Message<UID> message) throws DispatchException;

}
