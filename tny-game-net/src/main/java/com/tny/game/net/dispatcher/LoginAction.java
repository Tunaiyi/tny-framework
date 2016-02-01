package com.tny.game.net.dispatcher;

import com.tny.game.net.LoginCertificate;

public interface LoginAction {

    public LoginCertificate handleLogin(Response response);

}
