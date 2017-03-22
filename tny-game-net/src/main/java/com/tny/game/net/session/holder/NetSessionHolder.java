package com.tny.game.net.session.holder;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.session.NetSession;

import java.util.Optional;

public interface NetSessionHolder<UID, S extends NetSession<UID>> extends SessionHolder<UID> {


    /**
     * <p>
     * <p>
     * 添加指定的session<br>
     *
     * @param session     指定的session
     * @param certificate 登陆凭证
     * @throws ValidatorFailException
     */
    Optional<S> online(S session, LoginCertificate<UID> certificate) throws ValidatorFailException;

    // protected void disconnect(NetSession<?> session) {
    //     session.offline();
    //     this.fireDisconnectSession(new SessionChangeEvent<>(this, session));
    // }

}
