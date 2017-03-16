package com.tny.game.net.dispatcher;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.exception.ValidatorFailException;
import com.tny.game.net.session.SessionHolder;

public interface NetSessionHolder extends SessionHolder {


    /**
     * <p>
     * <p>
     * 添加指定的session<br>
     *
     * @param session     指定的session
     * @param certificate 登陆凭证
     * @throws ValidatorFailException
     */
    <T> boolean online(NetSession<T> session, LoginCertificate<T> certificate) throws ValidatorFailException;

    // protected void disconnect(NetSession<?> session) {
    //     session.offline();
    //     this.fireDisconnectSession(new SessionChangeEvent<>(this, session));
    // }

}
