package com.tny.game.net.base.listener;

import com.tny.game.net.session.Session;
import com.tny.game.net.session.SessionHolder;

public class SessionChangeEvent<T> {

    private SessionHolder sessionHolder;

    private Session<T> session;

    public SessionChangeEvent(SessionHolder sessionHolder, Session<T> session) {
        super();
        this.session = session;
        this.sessionHolder = sessionHolder;
    }

    public SessionHolder getSessionHolder() {
        return this.sessionHolder;
    }

    public Session<T> getSession() {
        return this.session;
    }

}
