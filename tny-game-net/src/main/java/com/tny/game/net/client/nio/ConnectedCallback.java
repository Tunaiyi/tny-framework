package com.tny.game.net.client.nio;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.ClientSession;

public interface ConnectedCallback {

    public LoginCertificate connected(boolean success, ClientSession session, Throwable cause);

}
