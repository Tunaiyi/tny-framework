package com.tny.game.net.base.listener;

import com.tny.game.net.dispatcher.Session;
import com.tny.game.net.dispatcher.SessionHolder;

public class SessionChangeEvent {

    private SessionHolder sessionHolder;

    private Session session;

    public SessionChangeEvent(SessionHolder sessionHolder, Session session) {
        super();
        this.session = session;
        this.sessionHolder = sessionHolder;
    }

    public SessionHolder getSessionHolder() {
        return this.sessionHolder;
    }

    public Session getSession() {
        return this.session;
    }

}
