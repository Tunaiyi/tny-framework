package com.tny.game.net.endpoint;

public enum EndpointStatus {

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
    CLOSE(3),

    //

    ;

    private final int id;

    EndpointStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
