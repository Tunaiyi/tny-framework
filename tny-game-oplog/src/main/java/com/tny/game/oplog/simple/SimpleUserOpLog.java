package com.tny.game.oplog.simple;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.Attributes;
import com.tny.game.oplog.ActionLog;
import com.tny.game.oplog.StuffSettleLog;
import com.tny.game.oplog.UserOpLog;
import com.tny.game.oplog.record.StuffRecord;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户操作日志
 *
 * @author KGTny
 */
public class SimpleUserOpLog extends UserOpLog {

    private long userID;

    private String openID;

    private String pf;

    private int serverID;

    private String name;

    private int level;

    private int vip;

    private DateTime createAt;

    private Attributes attributes;

    private List<ActionLog> actionLogs = new ArrayList<>();

    private Map<Integer, StuffSettleLog> stuffLogs = new HashMap<>();

    public SimpleUserOpLog(long userID, String pf, String openID, int serverID, String name, DateTime createAt, int level, int vip) {
        super();
        this.userID = userID;
        this.openID = openID;
        this.createAt = createAt;
        this.pf = pf;
        this.serverID = serverID;
        this.name = name;
        this.vip = vip;
        this.level = level;
    }

    @Override
    public long getUserID() {
        return this.userID;
    }

    @Override
    public int getServerID() {
        return this.serverID;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getVip() {
        return this.vip;
    }

    @Override
    public String getPF() {
        return pf;
    }

    @Override
    public String getOpenID() {
        return this.openID;
    }

    @Override
    public DateTime getCreateAt() {
        return createAt;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ActionLog> getActionLogs() {
        Collection<? extends ActionLog> logs = Collections.unmodifiableCollection(this.actionLogs);
        return (Collection<ActionLog>) logs;
    }

    @Override
    public Collection<StuffSettleLog> getStuffSettleLogs() {
        return stuffLogs.values();
    }

    @Override
    public Attributes attributes() {
        return this.attributes;
    }

    @Override
    protected ActionLog getActionLog(Action action) {
        for (int index = 0; index < this.actionLogs.size(); index++) {
            ActionLog log = this.actionLogs.get(index);
            if (log.getActionID() == action.getID())
                return log;
        }
        ActionLog actionLog = new SimpleActionLog(action);
        this.actionLogs.add(actionLog);
        return actionLog;
    }

    @Override
    protected StuffSettleLog getStuffSettleLog(int itemID) {
        return stuffLogs.computeIfAbsent(itemID, StuffRecord::new);
    }

}