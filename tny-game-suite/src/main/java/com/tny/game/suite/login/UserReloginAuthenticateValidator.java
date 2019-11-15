package com.tny.game.suite.login;

import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.springframework.stereotype.Component;

@Component
public class UserReloginAuthenticateValidator extends UserAuthenticateValidator {

    @Override
    public Certificate<Long> validate(Tunnel<Long> tunnel, Message<Long> message) throws CommandException {
        return checkUserLogin(tunnel, message, true);
    }

}
