package com.tny.game.net.message;

import com.tny.game.net.session.Session;

public interface NetMessage<UID> extends Message<UID> {

    void register(Session<UID> session);

}
