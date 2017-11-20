package com.tny.game.oplog;

import com.tny.game.base.item.Identifiable;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.behavior.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOpLogger implements OpLogger {

    private ThreadLocal<OpLog> localOpLog = new ThreadLocal<>();

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractOpLogger.class);

    @Override
    public boolean isLogging() {
        return this.localOpLog.get() != null;
    }

    protected OpLog getOpLog() {
        OpLog log = this.localOpLog.get();
        try {
            if (log == null) {
                log = this.createLog();
                this.localOpLog.set(log);
            }
        } catch (Exception e) {
            LOGGER.error("createLog exception", e);
        }
        return log;
    }

    public UserOpLog getUserLogger(long playerID) {
        OpLog log = this.getOpLog();
        if (log == null)
            return null;
        UserOpLog userOpLog = this.createUserOpLog(playerID);
        return log.putUserOpLog(userOpLog);
    }

    @Override
    public OpLogger logReceive(Item<?> item, Action action, long oldNum, long alter, long newNum) {
        if (item == null)
            return this;
        return logReceive(item.getPlayerID(), item.getID(), item.getItemID(), action, oldNum, alter, newNum);
    }

    @Override
    public OpLogger logReceive(long playerID, long id, ItemModel model, Action action, long oldNum, long alter, long newNum) {
        if (model == null)
            return this;
        return logReceive(playerID, id, model.getID(), action, oldNum, alter, newNum);
    }

    @Override
    public OpLogger logReceive(long playerID, long id, int itemID, Action action, long oldNum, long alter, long newNum) {
        try {
            action = this.transAction(action);
            UserOpLog log = this.getUserLogger(playerID);
            if (log == null)
                return this;
            log.logReceive(id, itemID, action, oldNum, alter, newNum);
        } catch (Exception e) {
            LOGGER.error("{} | {} | logReceive exception", playerID, action, e);
        }
        return this;
    }

    @Override
    public OpLogger logConsume(Item<?> item, Action action, long oldNum, long alter, long newNum) {
        if (item == null)
            return this;
        return logConsume(item.getPlayerID(), item.getID(), item.getItemID(), action, oldNum, alter, newNum);
    }

    @Override
    public OpLogger logConsume(long playerID, long id, ItemModel model, Action action, long oldNum, long alter, long newNum) {
        if (model == null)
            return this;
        return logConsume(playerID, id, model.getID(), action, oldNum, alter, newNum);
    }

    @Override
    public OpLogger logConsume(long playerID, long id, int itemID, Action action, long oldNum, long alter, long newNum) {
        try {
            action = this.transAction(action);
            UserOpLog log = this.getUserLogger(playerID);
            if (log == null)
                return this;
            log.logConsume(id, itemID, action, oldNum, alter, newNum);
        } catch (Exception e) {
            LOGGER.error("{} | {} | logConsume exception", playerID, action, e);
        }
        return this;
    }

    @Override
    public OpLogger logSnapshotByType(Identifiable item, Action action, SnapperType... types) {
        try {
            action = this.transAction(action);
            if (types.length == 0)
                this.doLogSnapshot(action, item);
            for (SnapperType type : types) {
                this.doLogSnapshot(action, item, type);
            }
        } catch (Exception e) {
            LOGGER.error("{} | {} | logSnapshot exception", item, action, e);
        }
        return this;
    }

    @Override
    @SafeVarargs
    public final OpLogger logSnapshotByClass(Identifiable item, Action action, Class<? extends Snapper>... snapperTypes) {
        try {
            action = this.transAction(action);
            if (snapperTypes.length == 0)
                this.doLogSnapshot(action, item);
            for (Class<? extends Snapper> type : snapperTypes) {
                this.doLogSnapshot(action, item, type);
            }
        } catch (Exception e) {
            LOGGER.error("{} | {} | logSnapshot exception", item, action, e);
        }
        return this;
    }

    @Override
    public OpLogger logSnapshot(Identifiable item, Action action) {
        action = this.transAction(action);
        this.doLogSnapshot(action, item);
        return this;
    }

    protected boolean logSnapshot(Action action, Snapshot snapshot) {
        action = this.transAction(action);
        UserOpLog log = this.getUserLogger(snapshot.getPlayerID());
        Snapshot old = log.getSnapshot(action, snapshot.getID(), snapshot.getType());
        if (old == null) {
            log.logSnapshot(action, snapshot);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected <S extends Snapshot> S getSnapshot(long playerID, long id, Action action, SnapshotType type) {
        UserOpLog log = this.getUserLogger(playerID);
        if (log == null)
            return null;
        return (S) log.getSnapshot(action, id, type);
    }

    protected abstract void doLogSnapshot(Action action, Identifiable item, SnapperType type);

    protected abstract void doLogSnapshot(Action action, Identifiable item, Class<? extends Snapper> type);

    protected abstract void doLogSnapshot(Action action, Identifiable item);

    protected OpLog pollLog() {
        OpLog log = this.localOpLog.get();
        this.localOpLog.remove();
        return log;
    }

    @Override
    public void submit() {
        OpLog log = this.pollLog();
        if (log != null) {
            this.doSubmit(log);
        }
    }

    protected Action transAction(Action action) {
        return action;
    }

    protected abstract void doSubmit(OpLog log);

    protected abstract UserOpLog createUserOpLog(long playerID);

    protected abstract OpLog createLog();

}
