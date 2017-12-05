package com.tny.game.oplog.log4j2.layout;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.oplog.StuffSettleLog;
import com.tny.game.oplog.record.StuffRecord;
import com.tny.game.oplog.record.UserStuffRecord;

import java.util.List;
import java.util.stream.Collectors;

import static com.tny.game.suite.base.ObjectAide.*;

@JsonAutoDetect(
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonUserStuffRecord {

    @JsonProperty(index = 1)
    private long rcid;

    @JsonProperty(index = 2)
    private long uid;

    @JsonProperty(index = 3)
    private String name;

    @JsonProperty(index = 4)
    private int sid;

    @JsonProperty(index = 5)
    private List<StuffRecord> stuffs;

    public JsonUserStuffRecord() {
    }

    @SuppressWarnings("unchecked")
    public JsonUserStuffRecord(long rcid, UserStuffRecord log) {
        this.rcid = rcid;
        this.uid = log.getUserID();
        this.name = log.getName();
        this.sid = log.getServerID();
        this.stuffs = log.getStuffLogs()
                .stream()
                .map(JsonUserStuffRecord::log2Record)
                .collect(Collectors.toList());
    }

    private static StuffRecord log2Record(StuffSettleLog log) {
        if (log instanceof StuffRecord)
            return as(log, StuffRecord.class);
        return new StuffRecord(log);
    }

    public long getRecordID() {
        return rcid;
    }

    public long getUserID() {
        return uid;
    }


    public int getServerID() {
        return sid;
    }

    public String getName() {
        return name;
    }

    public List<StuffRecord> getStuffs() {
        return stuffs;
    }
}
