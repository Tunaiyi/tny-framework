package com.tny.game.oplog.log4j2.layout;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.oplog.StuffSettleLog;
import com.tny.game.oplog.record.StuffRecord;
import com.tny.game.oplog.record.UserStuffRecord;

import java.util.List;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

@JsonAutoDetect(
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonUserStuffRecord {

    @JsonProperty(index = 0)
    private long rcid;

    @JsonProperty(index = 1)
    private long at;

    @JsonProperty(index = 2)
    private long uid;

    @JsonProperty(index = 3)
    private String name;

    @JsonProperty(index = 7)
    private int sid;

    @JsonProperty(index = 9)
    private int lv;

    @JsonProperty(index = 10)
    private String opid;

    @JsonProperty(index = 11)
    private String pf;

    @JsonProperty(index = 12)
    private int vip;

    @JsonProperty(index = 13)
    private List<StuffRecord> stuffs;

    public JsonUserStuffRecord() {
    }

    @SuppressWarnings("unchecked")
    public JsonUserStuffRecord(long rcid, UserStuffRecord log) {
        this.rcid = rcid;
        this.at = log.getAt();
        this.uid = log.getUserID();
        this.name = log.getName();
        this.sid = log.getServerID();
        this.lv = log.getLevel();
        this.opid = log.getOpenID();
        this.pf = log.getPF();
        this.vip = log.getVip();
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

    public long getAt() {
        return at;
    }

    public int getServerID() {
        return sid;
    }

    public String getPF() {
        return pf;
    }

    public int getVip() {
        return vip;
    }

    public String getOpenID() {
        return opid;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return lv;
    }

    public List<StuffRecord> getStuffs() {
        return stuffs;
    }
}
