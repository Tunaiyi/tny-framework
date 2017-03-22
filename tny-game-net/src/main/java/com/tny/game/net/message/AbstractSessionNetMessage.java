package com.tny.game.net.message;

import com.tny.game.net.session.Session;

public abstract class AbstractSessionNetMessage<UID> extends AbstractNetMessage<UID> {

    protected volatile Session<UID> session;

    @Override
    public void register(Session<UID> session) {
        this.session = session;
    }

    @Override
    protected AbstractSessionNetMessage<UID> setSession(Session<UID> session) {
        this.session = session;
        return this;
    }

    @Override
    public UID getUserID() {
        Session<UID> session = this.session;
        return session == null ? null : session.getUID();
    }

    @Override
    public String getUserGroup() {
        Session<UID> session = this.session;
        return session == null ? Session.DEFAULT_USER_GROUP : session.getGroup();
    }

}
