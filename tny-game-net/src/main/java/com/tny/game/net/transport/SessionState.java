package com.tny.game.net.transport;

public enum SessionState {

    /**
     * 在线
     */
    ONLINE(1),

    /**
     * 离线
     */
    OFFLINE(2),

    /**
     * 关闭
     */
    CLOSE(0),

    //

    ;

    private final int id;

    SessionState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
