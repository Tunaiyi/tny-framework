package com.tny.game.suite.login;

import com.tny.game.net.exception.CommandException;
import com.tny.game.net.message.Message;
import com.tny.game.net.transport.Certificate;
import com.tny.game.net.transport.Tunnel;

public class UserLoginAuthenticateValidator extends UserAuthenticateValidator {

    @Override
    public Certificate<Long> validate(Tunnel<Long> tunnel, Message<Long> message) throws CommandException {
        return checkUserLogin(tunnel, message, false);
    }

}
