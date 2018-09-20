package com.tny.game.suite.login;

import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.transport.message.Message;
import com.tny.game.net.transport.Certificate;
import com.tny.game.net.transport.Tunnel;

public class UserLoginAuthenticateProvider extends UserAuthenticateProvider {

    @Override
    public Certificate<Long> validate(Tunnel<Long> tunnel, Message<Long> message) throws DispatchException {
        return checkUserLogin(tunnel, message, false);
    }

}
