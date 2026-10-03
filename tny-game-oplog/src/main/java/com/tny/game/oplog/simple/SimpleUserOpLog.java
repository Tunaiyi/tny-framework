/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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