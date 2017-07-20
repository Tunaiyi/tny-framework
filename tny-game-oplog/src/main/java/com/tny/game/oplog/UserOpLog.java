package com.tny.game.oplog;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.Attributes;

import java.util.Collection;

public abstract class UserOpLog {

    /**
     * @return 日志所属用户ID
     */
    public abstract long getUserID();

    /**
     * @return vip
     */
    public abstract String getName();

    /**
     * @return 等级
     */
    public abstract int getLevel();

    /**
     * @return vip
     */
    public abstract int getVip();

    /**
     * @return 平台用户ID
     */
    public abstract String getOpenID();

    /**
     * @return 创建角色服务器ID
     */
    public abstract int getCreateSID();

    /**
     * @return 日志操作日志Map
     */
    public abstract Collection<ActionLog> getActionLogs();

    /**
     * @return 属性
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