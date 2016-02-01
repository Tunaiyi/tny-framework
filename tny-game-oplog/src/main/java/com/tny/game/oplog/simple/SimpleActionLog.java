package com.tny.game.oplog.simple;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.oplog.*;

import java.util.*;

public class SimpleActionLog extends ActionLog {

    private int action;

    private int behavior;

    private List<SimpleTradeLog> receiveLogs = new ArrayList<>();

    private List<SimpleTradeLog> consumeLogs = new ArrayList<>();

    private Map<Long, List<Snapshot>> snapshotMap = new HashMap<>();

    private List<Snapshot> snapshots = new ArrayList<>();

    protected SimpleActionLog(Action action) {
        super();
        this.action = action.getID();
        this.behavior = action.getBehavior().getID();
    }

    @Override
    public int getActionID() {
        return this.action;
    }

    @Override
    public int getBehaviorID() {
        return this.behavior;
    }

    @Override
    public Collection<TradeLog> getReceiveLogs() {
        Collection<? extends TradeLog> logs = this.receiveLogs;
        return Collections.unmodifiableCollection(logs);
    }

    @Override
    public Collection<TradeLog> getConsumeLogs() {
        Collection<? extends TradeLog> logs = this.consumeLogs;
        return Collections.unmodifiableCollection(logs);
    }

    private SimpleTradeLog findTradeLog(List<SimpleTradeLog> logs, long id) {
        for (int index = 0; index < logs.size(); index++) {
            SimpleTradeLog log = logs.get(index);
            if (log.getID() == id)
                return log;
        }
        return null;
    }

    @Override
    protected ActionLog logReceive(long id, int itemID, int oldNum, int alter, int newNum) {
        if (alter == 0)
            return this;
        SimpleTradeLog log = this.findTradeLog(this.receiveLogs, id);
        if (log != null) {
            log.merge(alter, newNum);
        } else {
            log = new SimpleTradeLog(id, itemID, OpTradeType.RECEIVE, oldNum, alter, newNum);
            this.receiveLogs.add(log);
        }
        return this;
    }

    @Override
    protected ActionLog logConsume(long id, int itemID, int oldNum, int alter, int newNum) {
        if (alter == 0)
            return this;
        SimpleTradeLog log = this.findTradeLog(this.consumeLogs, id);
        if (log != null) {
            log.merge(alter, newNum);
        } else {
            log = new SimpleTradeLog(id, itemID, OpTradeType.CONSUME, oldNum, alter, newNum);
            this.consumeLogs.add(log);
        }
        return this;
    }

    @Override
    protected ActionLog logSnapshot(Snapshot snapshot) {
        List<Snapshot> snaps = this.snapshotMap.get(snapshot.getID());
        if (snaps == null) {
            snaps = new ArrayList<>();
            this.snapshotMap.put(snapshot.getID(), snaps);
        }
        boolean exsist = false;
        for (Snapshot snap : snaps) {
            if (snap.getType() == snapshot.getType())
                exsist = true;
        }
        if (!exsist) {
            snaps.add(snapshot);
            this.snapshots.add(snapshot);
        }
        return this;
    }

    public Collection<Snapshot> getSnapshots() {
        return Collections.unmodifiableCollection(this.snapshots);
    }

    @Override
    protected Snapshot getSnapshot(long id, SnapshotType type) {
        List<Snapshot> snaps = this.snapshotMap.get(id);
        if (snaps == null) {
            snaps = new ArrayList<>();
            this.snapshotMap.put(id, snaps);
        }
        for (Snapshot snap : snaps) {
            if (snap.getType() == type)
                return snap;
        }
        return null;
    }

}
