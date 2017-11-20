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
     * @return 平台标识
     */
    public abstract String getPF();

    /**
     * @return 平台用户ID
     */
    public abstract String getOpenID();

    /**
     * @return 创建角色服务器ID
     */
    public abstract int getServerID();

    /**
     * @return 日志操作日志Map
     */
    public abstract Collection<ActionLog> getActionLogs();

    /**
     * @return 物品流动统计
     */
    public abstract Collection<StuffSettleLog> getStuffSettleLogs();

    /**
     * @return 属性
     */
    public abstract Attributes attributes();

    protected abstract ActionLog getActionLog(Action action);

    protected abstract StuffSettleLog getStuffFlowLog(int itemID);

    protected UserOpLog logReceive(long id, int itemID, Action action, long oldNum, long alter, long newNum) {
        ActionLog log = this.getActionLog(action);
        log.logReceive(id, itemID, oldNum, alter, newNum);
        getStuffFlowLog(itemID).receive(newNum, alter);
        return this;
    }

    protected UserOpLog logConsume(long id, int itemID, Action action, long oldNum, long alter, long newNum) {
        ActionLog log = this.getActionLog(action);
        log.logConsume(id, itemID, oldNum, alter, newNum);
        getStuffFlowLog(itemID).consume(newNum, alter);
        return this;
    }

    protected Snapshot getSnapshot(Action action, long id, SnapshotType type) {
        ActionLog log = this.getActionLog(action);
        return log.getSnapshot(id, type);
    }

    protected UserOpLog logSnapshot(Action action, Snapshot snapshot) {
        ActionLog log = this.getActionLog(action);
        log.logSnapshot(snapshot);
        return this;
    }

}