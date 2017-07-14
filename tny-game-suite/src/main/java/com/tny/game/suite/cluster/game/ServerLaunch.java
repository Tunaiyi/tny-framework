package com.tny.game.suite.cluster.game;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tny.game.common.utils.DateTimeAide;
import com.tny.game.protobuf.PBCommon.StringEntryProto;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import org.joda.time.DateTime;

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
        DateTime current = DateTime.now();
        this.launchDate = current.toString(DateTimeAide.DISPLAY_DATE_FORMAT);
        this.launchTime = current.toString(DateTimeAide.TIME_FORMAT);
    }

    public int getServerID() {
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
