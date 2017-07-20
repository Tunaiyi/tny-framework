package com.tny.game.suite.oplog.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.common.utils.DateTimeAide;
import com.tny.game.oplog.ActionLog;
import com.tny.game.oplog.OpLog;
import com.tny.game.oplog.OperateLog;
import com.tny.game.oplog.Snapshot;
import com.tny.game.oplog.SnapshotType;
import com.tny.game.oplog.StuffLog;
import com.tny.game.oplog.TradeLog;
import com.tny.game.oplog.UserOpLog;
import com.tny.game.suite.auto.snapshot.BaseSnapshot;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonAutoDetect(
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public class OperateLogDTO implements OperateLog {

    @JsonProperty(index = 0)
    private long at;

    @JsonProperty(index = 1)
    private long uid;

    @JsonProperty(index = 2)
    private String name;

    @JsonProperty(index = 3)
    private int acid;

    @JsonProperty(index = 4)
    private int sid;

    @JsonProperty(index = 7)
    private Object op;

    @JsonProperty(index = 8)
    private int lv;

    @JsonProperty(index = 9)
    private String opid;

    @JsonProperty(index = 10)
    private int vip;

    @JsonProperty(index = 11)
    private List<ReceiveLogDTO> revs;

    @JsonProperty(index = 12)
    private List<ConsumeLogDTO> coss;

    @JsonProperty(index = 13)
    private List<BaseSnapshot> snaps;

    @JsonProperty(index = 14)
    private int i;

    private String logID;

    private String type = "oplog";

    private DateTime logAt;

    private int date;

    public OperateLogDTO() {
    }

    @SuppressWarnings("unchecked")
    public OperateLogDTO(String logID, OpLog log, UserOpLog userOpLog, ActionLog actionLog, int index) {
        this.logID = logID;
        this.uid = userOpLog.getUserID();
        this.name = userOpLog.getName();
        this.lv = userOpLog.getLevel();
        this.sid = userOpLog.getCreateSID();
        this.opid = userOpLog.getOpenID();
        this.vip = userOpLog.getVip();
        //		this.mod = log.getModule();
        this.op = log.getProtocol();
        DateTime dateTime = log.getCreateAt();
        this.at = dateTime.getMillis();
        this.logAt = dateTime;
        this.date = DateTimeAide.date2Int(dateTime);
        this.acid = actionLog.getActionID();
        this.i = index;
        for (TradeLog tradeLog : actionLog.getReceiveLogs()) {
            if (this.revs == null)
                this.revs = new ArrayList<>();
            this.revs.add(new ReceiveLogDTO(tradeLog));
        }
        for (TradeLog tradeLog : actionLog.getConsumeLogs()) {
            if (this.coss == null)
                this.coss = new ArrayList<>();
            this.coss.add(new ConsumeLogDTO(tradeLog));
        }
        Collection<Snapshot> snaps = actionLog.getSnapshots();
        if (snaps != null && !snaps.isEmpty()) {
            this.snaps = new ArrayList<>();
            snaps.forEach(snap -> {
                if (snap instanceof BaseSnapshot)
                    this.snaps.add((BaseSnapshot) snap);
            });
        }
    }

    @Override
    public long getUserID() {
        return this.uid;
    }

    @Override
    public long getAt() {
        return this.at;
    }

    @Override
    public int getDate() {
        return this.date;
    }

    @Override
    public int getActionID() {
        return this.acid;
    }

    @Override
    public int getServerID() {
        return this.sid;
    }

    @Override
    public Object getModule() {
        return (Integer) this.op / 100;
    }

    @Override
    public Object getOperation() {
        return this.op;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getLevel() {
        return this.lv;
    }

    @Override
    public int getVip() {
        return this.vip;
    }

    @Override
    public String getOpenID() {
        return this.opid;
    }

    @Override
    public DateTime getLogAt() {
        return logAt == null ? this.logAt = new DateTime(this.at) : this.logAt;
    }

    @Override
    public List<? extends StuffLog> getRevs() {
        return this.revs;
    }

    @Override
    public List<? extends StuffLog> getCoss() {
        return this.coss;
    }

    @Override
    public List<? extends Snapshot> getSnaps() {
        return this.snaps;
    }

    @Override
    public String toString() {
        return "OperateLogDTO [uid=" + this.uid + ", name=" + this.name + ", acid=" + this.acid + ", sid=" + this.sid + ", at=" + this.at + ", op=" + this.op
                + ", lv=" + this.lv
                + ", revs=" + this.revs + ", coss=" + this.coss + ", snaps=" + this.snaps + "]";
    }

    @Override
    public String getLogID() {
        return logID;
    }

    // @Override
    // public Action getAction() {
    //     if (this.action != null)
    //         return this.action;
    //     this.action = Actions.of(this.acid);
    //     if (this.action == null)
    //         this.action = new UnknowAction(this.acid);
    //     return this.action;
    // }
    //
    // @Override
    // public String getFuncSysDesc() {
    //     Action action = this.getAction();
    //     return action.getFeature().getDesc();
    // }
    //
    // @Override
    // public String getBehaviorDesc() {
    //     Action action = this.getAction();
    //     return action.getBehavior().getDesc();
    // }
    //
    // @Override
    // public String getActionDesc() {
    //     Action action = this.getAction();
    //     return action.getDesc();
    // }

    // public String getActionTitle() {
    //     Action action = this.getAction();
    //     String text = "";
    //     if (action instanceof UnknowAction) {
    //         text = action.getDesc();
    //     } else {
    //         text = action.getDesc();
    //     }
    //     return text;
    // }

    public List<Snapshot> getSnapshotsByType(SnapshotType type) {
        List<Snapshot> snapshots = new ArrayList<>();
        for (Snapshot snapshot : this.snaps) {
            if (snapshot.getType() == type)
                snapshots.add(snapshot);
        }
        return snapshots;
    }

}
