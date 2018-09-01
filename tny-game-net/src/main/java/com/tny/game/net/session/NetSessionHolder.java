package com.tny.game.net.session;

import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.tunnel.NetTunnel;

public interface NetSessionHolder extends SessionHolder {

    /**
     * <p>
     * <p>
     * 添加指定的session<br>
     *
     * @param tunnel      注册tunnel
     * @param certificate 登陆凭证
     * @throws ValidatorFailException 认证异常
     */
    <U> boolean online(NetTunnel<U> tunnel, NetCertificate<U> certificate) throws ValidatorFailException;

}
