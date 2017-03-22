package com.tny.game.net.client.nio;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.session.Session;

public interface ConnectedCallback<UID> {

    LoginCertificate connected(boolean success, Session<UID> session, Throwable cause);

}
