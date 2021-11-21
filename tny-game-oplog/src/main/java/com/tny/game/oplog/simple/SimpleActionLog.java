package com.tny.game.oplog.simple;

import com.tny.game.basics.item.behavior.*;
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
		this.action = action.getId();
		this.behavior = action.getBehavior().getId();
	}

	@Override
	public int getActionId() {
		return this.action;
	}

	@Override
	public int getBehaviorId() {
		return this.behavior;
	}

	@Override
	public Collection<StuffTradeLog> getReceiveLogs() {
		Collection<? extends StuffTradeLog> logs = this.receiveLogs;
		return Collections.unmodifiableCollection(logs);
	}

	@Override
	public Collection<StuffTradeLog> getConsumeLogs() {
		Collection<? extends StuffTradeLog> logs = this.consumeLogs;
		return Collections.unmodifiableCollection(logs);
	}

	private SimpleTradeLog findTradeLog(List<SimpleTradeLog> logs, long id) {
		for (int index = 0; index < logs.size(); index++) {
			SimpleTradeLog log = logs.get(index);
            if (log.getId() == id) {
                return log;
            }
		}
		return null;
	}

	@Override
	protected ActionLog logReceive(long id, int itemId, long oldNum, long alter, long newNum) {
        if (alter == 0) {
            return this;
        }
		SimpleTradeLog log = this.findTradeLog(this.receiveLogs, id);
		if (log != null) {
			log.receive(alter, newNum);
		} else {
			log = new SimpleTradeLog(id, itemId, OpTradeType.RECEIVE, oldNum, alter, newNum);
			this.receiveLogs.add(log);
		}
		return this;
	}

	@Override
	protected ActionLog logConsume(long id, int itemId, long oldNum, long alter, long newNum) {
        if (alter == 0) {
            return this;
        }
		SimpleTradeLog log = this.findTradeLog(this.consumeLogs, id);
		if (log != null) {
			log.consume(alter, newNum);
		} else {
			log = new SimpleTradeLog(id, itemId, OpTradeType.CONSUME, oldNum, alter, newNum);
			this.consumeLogs.add(log);
		}
		return this;
	}

	@Override
	protected ActionLog logSnapshot(Snapshot snapshot) {
		List<Snapshot> snaps = this.snapshotMap.computeIfAbsent(snapshot.getId(), k -> new ArrayList<>());
		boolean exist = false;
		for (Snapshot snap : snaps) {
			if (snap.getType() == snapshot.getType()) {
				exist = true;
				break;
			}
		}
		if (!exist) {
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
            if (snap.getType() == type) {
                return snap;
            }
		}
		return null;
	}

}
