package com.tny.game.net.dispatcher;

import com.tny.game.net.LoginCertificate;

public interface NetSession extends Session {

    public void login(LoginCertificate loginInfo);

    /**
     * 断开连接
     */
    public void disconnect();

}