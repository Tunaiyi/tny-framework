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
