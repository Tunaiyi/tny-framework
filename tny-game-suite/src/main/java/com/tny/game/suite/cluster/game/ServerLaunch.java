package com.tny.game.suite.cluster.game;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tny.game.common.utils.*;
import com.tny.game.protobuf.PBCommon.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;

import java.time.Instant;

@ProtoEx(SuiteProtoIDs.CLUSTER_$SERVER_LAUNCH)
public class ServerLaunch {

    @ProtoExField(1)
    private int serverID;

    @ProtoExField(2)
    private String launchDate;

    @ProtoExField(3)
    private String launchTime;

    public ServerLaunch() {
    }

    public ServerLaunch(int serverID) {
        this.serverID = serverID;
        Instant current = Instant.now();
        this.launchDate = DateTimeAide.DISPLAY_DATE_FORMAT.format(current);
        this.launchTime = DateTimeAide.TIME_FORMAT.format(current);
    }

    public int getServerId() {
        return this.serverID;
    }

    public String getLaunchTime() {
        return this.launchTime;
    }

    public String getLaunchDate() {
        return this.launchDate;
    }

    @Override
    public String toString() {
        return "ServerLaunch [serverID=" + this.serverID + ", launchTime=" + this.launchTime + "]";
    }

    public static void main(String[] args) throws InvalidProtocolBufferException {
        byte[] data = StringEntryProto.newBuilder()
                .setIntValue(199)
                .build().toByteArray();
        StringEntryProto proto = StringEntryProto.parseFrom(data);
        System.out.println(proto.getKey());
    }

}
