package com.tny.game.suite.oplog.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.Behavior;
import com.tny.game.base.module.Feature;
import com.tny.game.base.module.Module;
import com.tny.game.oplog.*;
import com.tny.game.suite.base.Actions;
import com.tny.game.suite.base.Behaviors;
import com.tny.game.suite.base.module.Features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@JsonAutoDetect(
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE)
public class OperateLogDTO implements OperateLog {

    @JsonProperty(index = 1)
    private long uid;

    @JsonProperty(index = 2)
    private String name;

    @JsonProperty(index = 3)
    private int acid;

    @JsonProperty(index = 4)
    private int sid;

    @JsonProperty(index = 5)
    private long at;

    @JsonProperty(index = 7)
    private Object op;

    @JsonProperty(index = 8)
    private int lv;

    @JsonProperty(index = 10)
    private List<ReceiveLogDTO> revs;

    @JsonProperty(index = 11)
    private List<ConsumeLogDTO> coss;

    @JsonProperty(index = 12)
    private List<Snapshot> snaps;

    private Action action;

    public OperateLogDTO() {
    }

    @SuppressWarnings("unchecked")
    public OperateLogDTO(OpLog log, UserOpLog userOpLog, ActionLog actionLog) {
        this.uid = userOpLog.getUserID();
        this.name = userOpLog.getName();
        this.lv = userOpLog.getLevel();
        this.sid = userOpLog.getCreateSID();
        //		this.mod = log.getModule();
        this.op = log.getProtocol();
        this.at = log.getCreateAt();
        this.acid = actionLog.getActionID();
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
            this.snaps = new ArrayList<>(snaps);
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
    public int getLevel() {
        return this.lv;
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
    public Action getAction() {
        if (this.action != null)
            return this.action;
        this.action = Actions.of(this.acid);
        if (this.action == null)
            this.action = new UnknowAction(this.acid);
        return this.action;
    }

    @Override
    public String getFuncSysDesc() {
        Action action = this.getAction();
        return action.getFeature().getDesc();
    }

    @Override
    public String getBehaviorDesc() {
        Action action = this.getAction();
        return action.getBehavior().getDesc();
    }

    @Override
    public String getActionDesc() {
        Action action = this.getAction();
        return action.getDesc();
    }

    public String getActionTitle() {
        Action action = this.getAction();
        String text = "";
        if (action instanceof UnknowAction) {
            text = action.getDesc();
        } else {
            text = action.getDesc();
        }
        return text;
    }

    public List<Snapshot> getSnapshotsByType(SnapshotType type) {
        List<Snapshot> snapshots = new ArrayList<>();
        for (Snapshot snapshot : this.snaps) {
            if (snapshot.getType() == type)
                snapshots.add(snapshot);
        }
        return snapshots;
    }

    private static final class UnknowAction implements Action {

        private int ID;

        private Behavior behavior;

        private String name;

        private String desc;

        protected UnknowAction(int ID) {
            this.ID = ID;
            this.name = "UNKNOW_ACTION_" + this.ID;
            int behaviorID = this.ID / 1000;
            this.behavior = Behaviors.of(behaviorID);
            if (this.behavior == null) {
                this.behavior = new UnknowBehavior(behaviorID);
            }
            this.desc = this.behavior.getDesc() + "-未知操作( " + this.ID + " )";
        }

        @Override
        public Integer getID() {
            return this.ID;
        }

        @Override
        public Behavior getBehavior() {
            return this.behavior;
        }

        @Override
        public Feature getFeature() {
            return this.behavior.getFeature();
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public String getDesc() {
            return this.desc;
        }

    }

    private static final class UnknowBehavior implements Behavior {

        private int ID;

        private Feature feature;

        private String name;

        private String desc;

        protected UnknowBehavior(int ID) {
            this.ID = ID;
            this.name = "UNKNOW_BEHAVIOR_" + this.ID;
            int featureID = this.ID / 1000;
            this.feature = Features.ofUncheck(featureID);
            if (this.feature == null) {
                this.feature = new UnknowFeature(featureID);
            }
            this.desc = this.feature.getDesc() + "-未知行为( " + this.ID + " )";
        }

        @Override
        public Integer getID() {
            return this.ID;
        }

        @Override
        public Feature getFeature() {
            return feature;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public String getDesc() {
            return this.desc;
        }

        @Override
        public Action forAction(Object value) {
            return null;
        }

    }

    private static final class UnknowFeature implements Feature {

        private int ID;

        private String name;

        private String desc;

        protected UnknowFeature(int ID) {
            this.ID = ID;
            this.name = "UNKNOW_FEATURE_" + this.ID;
            this.desc = "未知系统( " + this.ID + " )";
        }

        @Override
        public Integer getID() {
            return this.ID;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Collection<Module> dependModules() {
            return Collections.emptyList();
        }

        @Override
        public String getDesc() {
            return this.desc;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public boolean isHasHandler() {
            return false;
        }

    }

}
