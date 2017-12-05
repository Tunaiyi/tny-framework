package com.tny.game.suite.cluster.game;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.utils.DateTimeAide;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.net.base.InetConnector;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

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

    @ProtoExField(14)
    private String serverScope;

    @ProtoExField(15)
    private List<InetConnector> publicConnectors;

    @ProtoExField(16)
    private List<InetConnector> privateConnectors;

    @ProtoExField(17)
    private String version;

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

    public String getServerScope() {
        return serverScope;
    }

    public List<InetConnector> getPublicConnectors() {
        if (publicConnectors == null)
            return ImmutableList.of();
        return publicConnectors;
    }

    public List<InetConnector> getPrivateConnectors() {
        if (privateConnectors == null)
            return ImmutableList.of();
        return privateConnectors;
    }

    public String getVersion() {
        return version;
    }

    public void checkConnectors() {
        if (CollectionUtils.isEmpty(this.publicConnectors) && StringUtils.isNoneBlank(this.publicIP)) {
            this.publicConnectors = ImmutableList.of(new InetConnector("default", "默认", this.publicIP, this.serverPort));
        }
        if (CollectionUtils.isEmpty(this.privateConnectors) && StringUtils.isNoneBlank(this.privateIP)) {
            this.privateConnectors = ImmutableList.of(new InetConnector("rmi", "RMI", this.privateIP, this.rmiPort));
        }
    }

    ServerOutline setServerID(int serverID) {
        this.serverID = serverID;
        return this;
    }

    ServerOutline setPublicIP(String publicIP) {
        this.publicIP = publicIP;
        return this;
    }

    ServerOutline setPrivateIP(String privateIP) {
        this.privateIP = privateIP;
        return this;
    }

    ServerOutline setServerPort(int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    ServerOutline setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
        return this;
    }

    ServerOutline setDbHost(String dbHost) {
        this.dbHost = dbHost;
        return this;
    }

    ServerOutline setServerType(String serverType) {
        this.serverType = serverType;
        return this;
    }

    ServerOutline setDbPort(int dbPort) {
        this.dbPort = dbPort;
        return this;
    }

    ServerOutline setDb(String db) {
        this.db = db;
        return this;
    }

    ServerOutline setMain(boolean main) {
        this.main = main;
        return this;
    }

    ServerOutline setFollowServer(int followServer) {
        this.followServer = followServer;
        return this;
    }

    ServerOutline setServerScope(String serverScope) {
        this.serverScope = serverScope;
        return this;
    }

    ServerOutline setPublicConnectors(Collection<InetConnector> publicConnectors) {
        this.publicConnectors = ImmutableList.copyOf(publicConnectors);
        return this;
    }

    ServerOutline setPrivateConnectors(Collection<InetConnector> privateConnectors) {
        this.privateConnectors = ImmutableList.copyOf(privateConnectors);
        return this;
    }

    ServerOutline setVersion(String version) {
        this.version = version;
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
        this.openDateTime = DateTime.parse(this.openDate, DateTimeAide.DATE_TIME_MIN_FORMAT);
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
        this.openDate = openDate.toString(DateTimeAide.DATE_TIME_MIN_FORMAT);
        return this;
    }

    public InetConnector getPublicConnector(String... ids) {
        return this.getPublicConnectors()
                .stream()
                .filter(c -> ArrayUtils.contains(ids, c.getId()))
                .findFirst()
                .orElse(null);
    }

    public InetConnector getPrivateConnector(String... ids) {
        return this.getPrivateConnectors()
                .stream()
                .filter(c -> ArrayUtils.contains(ids, c.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "ServerOutline [serverID=" + this.serverID + ", publicIP=" + this.publicIP + ", privateIP=" + this.privateIP + ", serverPort=" + this.serverPort + ", rmiPort=" + this.rmiPort + ", openDate=" + this.openDate + "]";
    }

    public boolean isHasDB() {
        return this.db != null && this.dbHost != null;
    }


    public static void main(String[] args) throws IOException {
        Map<String, LongAdder> count = new HashMap<>();
        File file = new File("/Users/KGTny/Desktop/test");
        List<String> lines = FileUtils.readLines(file, "UTF-8");
        lines.forEach(l -> count.put(l, new LongAdder()));
        lines.forEach(l -> count.get(l).increment());
        count.forEach((k, v) -> System.out.println(k + " = " + v));
    }

}
