package com.tny.game.suite.doors;


import com.tny.game.suite.cluster.ServerNode;
import org.apache.commons.lang3.StringUtils;

public class Entry {

    /**
     * 服务器编号
     */
    private int number;

    /**
     * 入口名称
     */
    private String name;

    /**
     * 所属区
     */
    private int zoneID;

    /**
     * 服务器ID
     */
    private ServerNode server;

    /**
     * 上线状态
     */
    private OnlineType onlineType;

    /**
     * 入口状态
     */
    private EntryState entryState;

    public Entry(int number, String name, int zoneID, ServerNode server, OnlineType onlineType, EntryState entryState) {
        this.number = number;
        this.name = name;
        this.zoneID = zoneID;
        this.server = server;
        this.onlineType = onlineType;
        this.entryState = entryState;
    }

    public Entry() {
    }

    public String getName() {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public int getZoneID() {
        return this.zoneID;
    }

    protected void setZoneID(int zoneID) {
        this.zoneID = zoneID;
    }

    public int getNumber() {
        return this.number;
    }

    protected void setNumber(int number) {
        this.number = number;
    }

    public ServerNode getServer() {
        return this.server;
    }

    void setServer(ServerNode server) {
        this.server = server;
    }

    public EntryState getEntryState() {
        return this.entryState;
    }

    void setEntryState(EntryState entryState) {
        this.entryState = entryState;
    }

    public OnlineType getOnlineType() {
        return this.onlineType;
    }

    void setOnlineType(OnlineType onlineType) {
        this.onlineType = onlineType;
    }

    public int getServerID() {
        return this.server.getServerID();
    }

    public void update(String name, ServerNode node, OnlineType type, EntryState state) {
        if (!StringUtils.isBlank(name))
            this.name = name;
        if (node != null)
            this.server = node;
        if (type != null)
            this.onlineType = type;
        if (state != null)
            this.entryState = state;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.number;
        result = prime * result + this.zoneID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Entry other = (Entry) obj;
        if (this.number != other.number)
            return false;
        if (this.zoneID != other.zoneID)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Entry [number=" + number + ", name=" + name + ", zoneID=" + zoneID + ", server=" + server + ", onlineType=" + onlineType + ", entryState=" + entryState + "]";
    }


}
