package com.tny.game.net.client.nio;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.message.Message;

public interface LoginAction<UID> {

    LoginCertificate handleLogin(Message<UID> response);

}
