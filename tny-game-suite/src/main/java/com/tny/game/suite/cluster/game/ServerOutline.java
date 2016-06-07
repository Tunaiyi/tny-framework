package com.tny.game.suite.cluster.game;

import com.tny.game.common.utils.DateTimeHelper;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

@ProtoEx(SuiteProtoIDs.CLUSTER_$SERVER_NODE)
public class ServerOutline {

    //service.properties # funs.god.server.id
    @ProtoExField(1)
    private int serverID;

    //service.properties # funs.god.server.public.host
    @ProtoExField(2)
    private String publicIP;

    //service.properties # funs.god.server.private.host
    @ProtoExField(3)
    private String privateIP;

    //service.properties # tny.server.config.bind.ips
    @ProtoExField(4)
    private int serverPort;

    //service.properties # tny.server.rmi.registryPort
    @ProtoExField(5)
    private int rmiPort;

    @ProtoExField(6)
    private String openDate;

    private volatile DateTime openDateTime;

    private volatile LocalDate openLocalDate;

    @ProtoExField(8)
    private String dbHost;

    @ProtoExField(9)
    private int dbPort;

    @ProtoExField(10)
    private String db;

    @ProtoExField(11)
    private String serverType;

    @ProtoExField(12)
    private boolean main;

    @ProtoExField(13)
    private int followServer;

    public ServerOutline() {
    }

    public int getServerID() {
        return this.serverID;
    }

    public String getPublicIP() {
        return this.publicIP;
    }

    public String getServerType() {
        return this.serverType;
    }

    public String getPrivateIP() {
        return this.privateIP;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public boolean isMain() {
        return this.main;
    }

    public int getFollowServer() {
        return this.followServer;
    }

    public int getRmiPort() {
        return this.rmiPort;
    }

    public ServerOutline setServerID(int serverID) {
        this.serverID = serverID;
        return this;
    }

    public ServerOutline setPublicIP(String publicIP) {
        this.publicIP = publicIP;
        return this;
    }

    public ServerOutline setPrivateIP(String privateIP) {
        this.privateIP = privateIP;
        return this;
    }

    public ServerOutline setServerPort(int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    public ServerOutline setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
        return this;
    }

    protected ServerOutline setDbHost(String dbHost) {
        this.dbHost = dbHost;
        return this;
    }

    protected ServerOutline setServerType(String serverType) {
        this.serverType = serverType;
        return this;
    }

    protected ServerOutline setDbPort(int dbPort) {
        this.dbPort = dbPort;
        return this;
    }

    protected ServerOutline setDb(String db) {
        this.db = db;
        return this;
    }

    protected ServerOutline setMain(boolean main) {
        this.main = main;
        return this;
    }

    protected ServerOutline setFollowServer(int followServer) {
        this.followServer = followServer;
        return this;
    }

    public String getOpenDate() {
        return this.openDate;
    }

    public LocalDate getOpenLocalDate() {
        if (this.openLocalDate != null)
            return this.openLocalDate;
        this.openLocalDate = new LocalDate(this.getOpenDateTime());
        return this.openLocalDate;
    }

    public DateTime getOpenDateTime() {
        if (this.openDateTime != null)
            return this.openDateTime;
        this.openDateTime = DateTime.parse(this.openDate, DateTimeHelper.SIMPLE_DATE_TIME_FORMAT);
        return this.openDateTime;
    }

    public String getDbHost() {
        return this.dbHost;
    }

    public int getDbPort() {
        return this.dbPort;
    }

    public String getDb() {
        return this.db;
    }

    public ServerOutline setOpenDate(DateTime openDate) {
        this.openDate = openDate.toString(DateTimeHelper.SIMPLE_DATE_TIME_FORMAT);
        return this;
    }

    @Override
    public String toString() {
        return "ServerOutline [serverID=" + this.serverID + ", publicIP=" + this.publicIP + ", privateIP=" + this.privateIP + ", serverPort=" + this.serverPort + ", rmiPort=" + this.rmiPort
                + ", openDate=" + this.openDate + "]";
    }

    public boolean isHasDB() {
        return this.db != null && this.dbHost != null;
    }

}
