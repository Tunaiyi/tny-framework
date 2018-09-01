package com.tny.game.suite.login;

import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.NetCertificate;
import com.tny.game.net.tunnel.Tunnel;

public class UserReloginAuthProvider extends UserAuthProvider {

    @Override
    public NetCertificate<Long> validate(Tunnel<Long> tunnel, Message<Long> message) throws DispatchException {
        return checkUserLogin(tunnel, message, true);
    }

}
