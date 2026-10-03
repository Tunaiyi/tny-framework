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

package com.tny.game.oplog;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

import java.time.Instant;
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
    public abstract Instant getCreateAt();

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

    protected abstract StuffSettleLog getStuffSettleLog(int itemId);

    protected UserOpLog logReceive(long id, int itemId, Action action, long oldNum, long alter, long newNum) {
        ActionLog log = this.getActionLog(action);
        log.logReceive(id, itemId, oldNum, alter, newNum);
        return this;
    }

    protected UserOpLog logConsume(long id, int itemId, Action action, long oldNum, long alter, long newNum) {
        ActionLog log = this.getActionLog(action);
        log.logConsume(id, itemId, oldNum, alter, newNum);
        return this;
    }

    protected UserOpLog settleReceive(int itemId, long alter, long newNum) {
        getStuffSettleLog(itemId).receive(newNum, alter);
        return this;
    }

    protected UserOpLog settleConsume(int itemId, long alter, long newNum) {
        getStuffSettleLog(itemId).consume(newNum, alter);
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