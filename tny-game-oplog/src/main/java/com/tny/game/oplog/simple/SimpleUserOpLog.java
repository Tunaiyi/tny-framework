package com.tny.game.oplog.simple;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.Attributes;
import com.tny.game.oplog.ActionLog;
import com.tny.game.oplog.UserOpLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 用户操作日志
 *
 * @author KGTny
 */
public class SimpleUserOpLog extends UserOpLog {

    private long userID;

    private String openID;

    private int createSID;

    private String name;

    private int level;

    private int vip;

    private Attributes attributes;

    private List<ActionLog> actionLogs = new ArrayList<>();

    public SimpleUserOpLog(long userID, String openID, int createSID, String name, int level, int vip) {
        super();
        this.userID = userID;
        this.openID = openID;
        this.createSID = createSID;
        this.name = name;
        this.vip = vip;
        this.level = level;
    }

    @Override
    public long getUserID() {
        return this.userID;
    }

    @Override
    public int getCreateSID() {
        return this.createSID;
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
    public String getOpenID() {
        return this.openID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ActionLog> getActionLogs() {
        Collection<? extends ActionLog> logs = Collections.unmodifiableCollection(this.actionLogs);
        return (Collection<ActionLog>) logs;
    }

    @Override
    public Attributes attributes() {
        return this.attributes;
    }

    @Override
    protected ActionLog getBaseActionLog(Action action) {
        for (int index = 0; index < this.actionLogs.size(); index++) {
            ActionLog log = this.actionLogs.get(index);
            if (log.getActionID() == action.getID())
                return log;
        }
        ActionLog actionLog = new SimpleActionLog(action);
        this.actionLogs.add(actionLog);
        return actionLog;
    }

}