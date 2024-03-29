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

package com.tny.game.oplog.simple;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;
import com.tny.game.oplog.*;
import com.tny.game.oplog.record.*;

import java.time.Instant;
import java.util.*;

/**
 * 用户操作日志
 *
 * @author KGTny
 */
public class SimpleUserOpLog extends UserOpLog {

    private long userId;

    private String openId;

    private String pf;

    private int serverId;

    private String name;

    private int level;

    private int vip;

    private Instant createAt;

    private Attributes attributes;

    private List<ActionLog> actionLogs = new ArrayList<>();

    private Map<Integer, StuffSettleLog> stuffLogs = new HashMap<>();

    public SimpleUserOpLog(long userId, String pf, String openId, int serverId, String name, Instant createAt, int level, int vip) {
        super();
        this.userId = userId;
        this.openId = openId;
        this.createAt = createAt;
        this.pf = pf;
        this.serverId = serverId;
        this.name = name;
        this.vip = vip;
        this.level = level;
    }

    @Override
    public long getUserId() {
        return this.userId;
    }

    @Override
    public int getServerId() {
        return this.serverId;
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
        return this.pf;
    }

    @Override
    public String getOpenId() {
        return this.openId;
    }

    @Override
    public Instant getCreateAt() {
        return this.createAt;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ActionLog> getActionLogs() {
        Collection<? extends ActionLog> logs = Collections.unmodifiableCollection(this.actionLogs);
        return (Collection<ActionLog>) logs;
    }

    @Override
    public Collection<StuffSettleLog> getStuffSettleLogs() {
        return this.stuffLogs.values();
    }

    @Override
    public Attributes attributes() {
        return this.attributes;
    }

    @Override
    protected ActionLog getActionLog(Action action) {
        for (int index = 0; index < this.actionLogs.size(); index++) {
            ActionLog log = this.actionLogs.get(index);
            if (log.getActionId() == action.getId()) {
                return log;
            }
        }
        ActionLog actionLog = new SimpleActionLog(action);
        this.actionLogs.add(actionLog);
        return actionLog;
    }

    @Override
    protected StuffSettleLog getStuffSettleLog(int itemId) {
        return this.stuffLogs.computeIfAbsent(itemId, StuffRecord::new);
    }

}