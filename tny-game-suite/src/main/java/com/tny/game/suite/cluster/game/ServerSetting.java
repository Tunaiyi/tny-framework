package com.tny.game.suite.cluster.game;

import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;

@ProtoEx(SuiteProtoIDs.CLUSTER_$SERVER_SETTING)
public class ServerSetting {

    @ProtoExField(1)
    private int serverId;

    @ProtoExField(2)
    private ServerState serverState;

    @ProtoExField(3)
    private String featureVersion;

    @ProtoExField(4)
    private String clientVersion;

    @ProtoExField(5)
    private String name;

    @ProtoExField(6)
    private String properties;

    public ServerSetting() {
    }

    public ServerSetting(ServerOutline outline) {
        this.setName("s" + outline.getServerId() + " 服")
                .setServerId(outline.getServerId())
                .setClientVersion("")
                .setServerState(ServerState.OFFLINE);
    }

    public String getName() {
        return this.name;
    }

    public ServerSetting setName(String name) {
        this.name = name;
        return this;
    }

    public int getServerId() {
        return this.serverId;
    }

    protected ServerSetting setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public ServerState getServerState() {
        return this.serverState;
    }

    public ServerSetting setServerState(ServerState serverState) {
        this.serverState = serverState;
        return this;
    }

    public String getClientVersion() {
        return this.clientVersion;
    }

    public ServerSetting setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
        return this;
    }

    public String getFeatureVersion() {
        return featureVersion;
    }

    public ServerSetting setFeatureVersion(String featureVersion) {
        this.featureVersion = featureVersion;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.serverId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ServerSetting other = (ServerSetting) obj;
        if (this.serverId != other.serverId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ServerSetting{" + "serverId=" + serverId +
               ", serverState=" + serverState +
               ", featureVersion='" + featureVersion + '\'' +
               ", clientVersion='" + clientVersion + '\'' +
               ", name='" + name + '\'' +
               '}';
    }

}
