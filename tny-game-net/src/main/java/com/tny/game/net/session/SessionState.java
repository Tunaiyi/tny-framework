package com.tny.game.net.session;

public enum SessionState {

    ONLINE(1),

    OFFLINE(2),

    INVALID(0);

    private final int id;

    SessionState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
