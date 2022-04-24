package com.tny.game.oplog.log4j2.layout;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.oplog.*;
import com.tny.game.oplog.record.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonAutoDetect(
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonOperateRecord {

    public static final Logger LOGGER = LoggerFactory.getLogger(JsonOperateRecord.class);

    @JsonProperty(index = 0)
    private long rcid;

    @JsonProperty(index = 1)
    private long at;

    @JsonProperty(index = 2)
    private long uid;

    @JsonProperty(index = 3)
    private String name;

    @JsonProperty(index = 4)
    private int acid;

    @JsonProperty(index = 7)
    private int sid;

    @JsonProperty(index = 8)
    private Object op;

    @JsonProperty(index = 9)
    private int lv;

    @JsonProperty(index = 10)
    private String opid;

    @JsonProperty(index = 11)
    private String pf;

    @JsonProperty(index = 12)
    private int vip;

    @JsonProperty(index = 13)
    private List<ReceiveRecord> revs;

    @JsonProperty(index = 14)
    private List<ConsumeRecord> coss;

    @JsonProperty(index = 15)
    private List<String> snaps;

    public JsonOperateRecord() {
    }

    @SuppressWarnings("unchecked")
    public JsonOperateRecord(long rcid, OperateRecord log, ObjectMapper mapper) {
        this.rcid = rcid;
        this.at = log.getAt();
        this.uid = log.getUserId();
        this.name = log.getName();
        this.acid = log.getActionId();
        this.sid = log.getServerId();
        this.op = log.getOperation();
        this.lv = log.getLevel();
        this.opid = log.getOpenId();
        this.pf = log.getPF();
        this.vip = log.getVip();
        this.revs = conver(log.getReceiveLog(), ReceiveRecord::new);
        this.coss = conver(log.getConsumeLogs(), ConsumeRecord::new);
        Collection<Snapshot> snapshots = log.getSnapshots();
        this.snaps = CollectionUtils.isEmpty(snapshots) ? null : snapshots.stream()
                .map(snap -> {
                    try {
                        return mapper.writeValueAsString(snap);
                    } catch (Throwable e) {
                        LOGGER.error("", e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <T extends StuffTradeLog> List<T> conver(Collection<StuffTradeLog> logs, Function<StuffTradeLog, T> builder) {
        if (logs == null) {
            return null;
        }
        return logs.stream()
                .map(builder)
                .collect(Collectors.toList());

    }

    public long getRecordId() {
        return rcid;
    }

    public long getUserId() {
        return uid;
    }

    public long getAt() {
        return at;
    }

    public int getServerId() {
        return sid;
    }

    public String getPF() {
        return pf;
    }

    public int getVip() {
        return vip;
    }

    public String getOpenId() {
        return opid;
    }

    public String getName() {
        return name;
    }

    public int getActionId() {
        return acid;
    }

    public Object getControllerId() {
        return op;
    }

    public int getLevel() {
        return lv;
    }

    public List<ReceiveRecord> getReveives() {
        return Collections.unmodifiableList(revs);
    }

    public List<ConsumeRecord> getConsumes() {
        return Collections.unmodifiableList(coss);
    }

    public List<String> getSnapshots() {
        return snaps;
    }

    public String toString() {
        return "OperateLogDTO [uid=" + this.uid + ", name=" + this.name + ", acid=" + this.acid + ", sid=" + this.sid + ", at=" + this.at + ", op=" +
                this.op
                + ", lv=" + this.lv
                + ", revs=" + this.revs + ", coss=" + this.coss + ", snaps=" + this.snaps + "]";
    }

}
