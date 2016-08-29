package com.tny.game.net.kafka;

/**
 * Created by Kun Yang on 16/8/10.
 */
public class KafkaServerInfo {

    private int id;

    private String serverType;

    public KafkaServerInfo(String serverType, int id) {
        this.id = id;
        this.serverType = serverType;
    }

    public int getID() {
        return id;
    }

    public String getServerType() {
        return serverType;
    }

}
