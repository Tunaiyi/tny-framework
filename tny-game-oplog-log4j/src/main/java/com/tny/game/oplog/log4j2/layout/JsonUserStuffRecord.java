package com.tny.game.oplog.log4j2.layout;

import com.fasterxml.jackson.annotation.*;
import com.tny.game.oplog.*;
import com.tny.game.oplog.record.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

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
        this.uid = log.getUserId();
        this.name = log.getName();
        this.sid = log.getServerId();
        this.stuffs = log.getStuffLogs()
                .stream()
                .map(JsonUserStuffRecord::log2Record)
                .collect(Collectors.toList());
    }

    private static StuffRecord log2Record(StuffSettleLog log) {
        if (log instanceof StuffRecord) {
            return as(log, StuffRecord.class);
        }
        return new StuffRecord(log);
    }

    public long getRecordId() {
        return rcid;
    }

    public long getUserId() {
        return uid;
    }

    public int getServerId() {
        return sid;
    }

    public String getName() {
        return name;
    }

    public List<StuffRecord> getStuffs() {
        return stuffs;
    }

}
