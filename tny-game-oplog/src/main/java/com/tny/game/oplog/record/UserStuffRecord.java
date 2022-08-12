/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.oplog.record;

import com.tny.game.oplog.*;

import java.time.Instant;
import java.util.*;

/**
 *
 */
public class UserStuffRecord extends AbstractLog {

    /**
     * 日志类型
     */
    public static final String TYPE = "stuff_flow";

    private List<StuffSettleLog> stuffLogs;

    @SuppressWarnings("unchecked")
    public UserStuffRecord(String logID, OpLog log, UserOpLog userOpLog) {
        super(TYPE, logID, log, userOpLog);
    }

    public Collection<StuffSettleLog> getStuffLogs() {
        return this.userOpLog.getStuffSettleLogs();
    }

    public Instant getCreateAt() {
        return this.userOpLog.getCreateAt();
    }

}
