package com.tny.game.oplog;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.Attributes;

import java.util.Collection;

public abstract class UserOpLog {

    /**
     * 日志所属用户ID
     *
     * @return
     */
    public abstract long getUserID();

    /**
     * 玩家名字
     *
     * @return
     */
    public abstract String getName();

    /**
     * 等级
     */
    public abstract int getLevel();

    /**
     * vip
     *
     * @return
     */
    public abstract int getVip();

    /**
     * 创建角色服务器ID
     *
     * @return
     */
    public abstract int getCreateSID();

    /**
     * 日志操作日志Map
     *
     * @return
     */
    public abstract Collection<ActionLog> getActionLogs();

    /**
     * 属性
     *
     * @return
     */
    public abstract Attributes attributes();

    protected abstract ActionLog getBaseActionLog(Action action);

    protected UserOpLog logReceive(long id, int itemID, Action action, long oldNum, long alter, long newNum) {
        ActionLog log = this.getBaseActionLog(action);
        log.logReceive(id, itemID, oldNum, alter, newNum);
        return this;
    }

    protected UserOpLog logConsume(long id, int itemID, Action action, long oldNum, long alter, long newNum) {
        ActionLog log = this.getBaseActionLog(action);
        log.logConsume(id, itemID, oldNum, alter, newNum);
        return this;

    }

    protected Snapshot getSnapshot(Action action, long id, SnapshotType type) {
        ActionLog log = this.getBaseActionLog(action);
        return log.getSnapshot(id, type);
    }

    protected UserOpLog logSnapshot(Action action, Snapshot snapshot) {
        ActionLog log = this.getBaseActionLog(action);
        log.logSnapshot(snapshot);
        return this;
    }

}