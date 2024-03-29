/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.oplog;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import org.slf4j.*;

public abstract class AbstractOpLogger implements OpLogger {

    private final ThreadLocal<OpLog> localOpLog = new ThreadLocal<>();

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractOpLogger.class);

    @Override
    public boolean isLogged() {
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

    public UserOpLog getUserLogger(long playerId) {
        OpLog log = this.getOpLog();
        if (log == null) {
            return null;
        }
        UserOpLog userOpLog = this.createUserOpLog(playerId);
        return log.putUserOpLog(userOpLog);
    }

    @Override
    public OpLogger logReceive(Item<?> item, Action action, long oldNum, long alter, long newNum) {
        if (item == null) {
            return this;
        }
        return logReceive(item.getPlayerId(), item.getId(), item.getModelId(), action, oldNum, alter, newNum);
    }

    @Override
    public OpLogger logReceive(long playerId, long id, ItemModel model, Action action, long oldNum, long alter, long newNum) {
        if (model == null) {
            return this;
        }
        return logReceive(playerId, id, model.getId(), action, oldNum, alter, newNum);
    }

    @Override
    public OpLogger logReceive(long playerId, long id, int modelId, Action action, long oldNum, long alter, long newNum) {
        try {
            action = this.transAction(action);
            UserOpLog log = this.getUserLogger(playerId);
            if (log == null) {
                return this;
            }
            log.logReceive(id, modelId, action, oldNum, alter, newNum);
        } catch (Exception e) {
            LOGGER.error("{} | {} | logReceive exception", playerId, action, e);
        }
        return this;
    }

    @Override
    public OpLogger settleReceive(Item<?> item, long alter, long newNum) {
        if (item == null) {
            return this;
        }
        return settleReceive(item.getPlayerId(), item.getModelId(), alter, newNum);
    }

    @Override
    public OpLogger settleReceive(long playerId, ItemModel model, long alter, long newNum) {
        if (model == null) {
            return this;
        }
        return settleReceive(playerId, model.getId(), alter, newNum);
    }

    @Override
    public OpLogger settleReceive(long playerId, int itemId, long alter, long newNum) {
        try {
            UserOpLog log = this.getUserLogger(playerId);
            if (log == null) {
                return this;
            }
            log.settleReceive(itemId, alter, newNum);
        } catch (Exception e) {
            LOGGER.error("{} | {} | settleReceive exception", playerId, e);
        }
        return this;
    }

    @Override
    public OpLogger logConsume(Item<?> item, Action action, long oldNum, long alter, long newNum) {
        if (item == null) {
            return this;
        }
        return logConsume(item.getPlayerId(), item.getId(), item.getModelId(), action, oldNum, alter, newNum);
    }

    @Override
    public OpLogger logConsume(long playerId, long id, ItemModel model, Action action, long oldNum, long alter, long newNum) {
        if (model == null) {
            return this;
        }
        return logConsume(playerId, id, model.getId(), action, oldNum, alter, newNum);
    }

    @Override
    public OpLogger logConsume(long playerId, long id, int modelId, Action action, long oldNum, long alter, long newNum) {
        try {
            action = this.transAction(action);
            UserOpLog log = this.getUserLogger(playerId);
            if (log == null) {
                return this;
            }
            log.logConsume(id, modelId, action, oldNum, alter, newNum);
        } catch (Exception e) {
            LOGGER.error("{} | {} | logConsume exception", playerId, action, e);
        }
        return this;
    }

    @Override
    public OpLogger settleConsume(Item<?> item, long alter, long newNum) {
        if (item == null) {
            return this;
        }
        return settleConsume(item.getPlayerId(), item.getModelId(), alter, newNum);
    }

    @Override
    public OpLogger settleConsume(long playerId, ItemModel model, long alter, long newNum) {
        if (model == null) {
            return this;
        }
        return settleConsume(playerId, model.getId(), alter, newNum);
    }

    @Override
    public OpLogger settleConsume(long playerId, int itemId, long alter, long newNum) {
        try {
            UserOpLog log = this.getUserLogger(playerId);
            if (log == null) {
                return this;
            }
            log.settleConsume(itemId, alter, newNum);
        } catch (Exception e) {
            LOGGER.error("{} | {} | settleReceive exception", playerId, e);
        }
        return this;
    }

    @Override
    public OpLogger logSnapshotByType(Any item, Action action, SnapperType... types) {
        try {
            action = this.transAction(action);
            if (types.length == 0) {
                this.doLogSnapshot(action, item);
            }
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
    public final OpLogger logSnapshotByClass(Any item, Action action, Class<? extends Snapper<?, ?>>... snapperTypes) {
        try {
            action = this.transAction(action);
            if (snapperTypes.length == 0) {
                this.doLogSnapshot(action, item);
            }
            for (Class<? extends Snapper> type : snapperTypes) {
                this.doLogSnapshot(action, item, type);
            }
        } catch (Exception e) {
            LOGGER.error("{} | {} | logSnapshot exception", item, action, e);
        }
        return this;
    }

    @Override
    public OpLogger logSnapshot(Any item, Action action) {
        action = this.transAction(action);
        this.doLogSnapshot(action, item);
        return this;
    }

    protected boolean logSnapshot(Action action, Snapshot snapshot) {
        action = this.transAction(action);
        UserOpLog log = this.getUserLogger(snapshot.getPlayerId());
        Snapshot old = log.getSnapshot(action, snapshot.getId(), snapshot.getType());
        if (old == null) {
            log.logSnapshot(action, snapshot);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected <S extends Snapshot> S getSnapshot(long playerId, long id, Action action, SnapshotType type) {
        UserOpLog log = this.getUserLogger(playerId);
        if (log == null) {
            return null;
        }
        return (S) log.getSnapshot(action, id, type);
    }

    protected abstract void doLogSnapshot(Action action, Any item, SnapperType type);

    @SuppressWarnings("rawtypes")
    protected abstract void doLogSnapshot(Action action, Any item, Class<? extends Snapper> type);

    protected abstract void doLogSnapshot(Action action, Any item);

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

    protected abstract UserOpLog createUserOpLog(long playerId);

    protected abstract OpLog createLog();

}
