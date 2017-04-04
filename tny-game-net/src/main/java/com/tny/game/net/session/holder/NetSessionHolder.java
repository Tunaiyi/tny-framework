package com.tny.game.net.session.holder;

import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.session.LoginCertificate;
import com.tny.game.net.session.NetSession;

public interface NetSessionHolder extends SessionHolder {


    /**
     * <p>
     * <p>
     * 添加指定的session<br>
     *
     * @param session     指定的session
     * @param certificate 登陆凭证
     * @throws ValidatorFailException 认证异常
     */
    <U> boolean online(NetSession<U> session, LoginCertificate<U> certificate) throws ValidatorFailException;

    // protected void disconnect(NetSession<?> session) {
    //     session.offline();
    //     this.fireDisconnectSession(new SessionChangeEvent<>(this, session));
    // }

}
