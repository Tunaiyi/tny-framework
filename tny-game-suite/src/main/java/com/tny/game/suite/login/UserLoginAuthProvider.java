package com.tny.game.suite.login;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.exception.DispatchException;

import java.util.List;

public class UserLoginAuthProvider extends UserAuthProvider {


    public UserLoginAuthProvider(List<Integer> authProtocols) {
        super("user-login-auth-provider", authProtocols);
    }

    @Override
    public LoginCertificate validate(Request request) throws DispatchException {
        return checkUserLogin(request, false);
    }

}
