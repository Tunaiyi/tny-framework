package com.tny.game.oplog;

import com.tny.game.base.item.behavior.*;
import com.tny.game.common.context.*;
import org.joda.time.DateTime;

import java.util.Collection;

public abstract class UserOpLog {

    /**
     * @return 日志所属用户ID
     */
    public abstract long getUserId();

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
    public abstract String getOpenId();

    /**
     * @return 创建角色服务器ID
     */
    public abstract int getServerId();

    /**
     * @return 创建角色时间
     */
    public abstract DateTime getCreateAt();

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

    protected abstract StuffSettleLog getStuffSettleLog(int itemID);

    protected UserOpLog logReceive(long id, int itemID, Action action, long oldNum, long alter, long newNum) {
        ActionLog log = this.getActionLog(action);
        log.logReceive(id, itemID, oldNum, alter, newNum);
        return this;
    }

    protected UserOpLog logConsume(long id, int itemID, Action action, long oldNum, long alter, long newNum) {
        ActionLog log = this.getActionLog(action);
        log.logConsume(id, itemID, oldNum, alter, newNum);
        return this;
    }

    protected UserOpLog settleReceive(int itemID, long alter, long newNum) {
        getStuffSettleLog(itemID).receive(newNum, alter);
        return this;
    }

    protected UserOpLog settleConsume(int itemID, long alter, long newNum) {
        getStuffSettleLog(itemID).consume(newNum, alter);
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