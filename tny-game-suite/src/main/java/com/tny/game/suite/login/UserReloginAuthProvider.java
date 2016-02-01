package com.tny.game.suite.login;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.exception.DispatchException;

import java.util.List;

public class UserReloginAuthProvider extends UserAuthProvider {

    public UserReloginAuthProvider(List<Integer> authProtocols) {
        super("user-relogin-auth-provider", authProtocols);
    }

    @Override
    public LoginCertificate validate(Request request) throws DispatchException {
        return checkUserLogin(request, true);
    }

}
