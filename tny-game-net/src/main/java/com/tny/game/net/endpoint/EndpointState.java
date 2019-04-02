package com.tny.game.net.endpoint;

public enum EndpointState {

    /**
     * 初始化
     */
    INIT(0),

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

    EndpointState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
