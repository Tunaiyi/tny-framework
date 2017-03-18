package com.tny.game.net.session.mobile;

enum MobileSessionState {

    ONLINE(1),

    OFFLINE(2),

    INVALID(0);

    private final int id;

    MobileSessionState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
