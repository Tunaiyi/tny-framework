package com.tny.game.net.session.holder;

import com.tny.game.net.common.session.CommonSessionHolder;
import com.tny.game.net.session.NetSession;

import java.rmi.server.UID;

public class DefaultSessionHolder<S extends NetSession<UID>> extends CommonSessionHolder<UID, S> {

    public DefaultSessionHolder(long clearInterval) {
        super(clearInterval);
    }

}
