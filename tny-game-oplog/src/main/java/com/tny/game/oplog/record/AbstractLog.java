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

import com.tny.game.common.utils.*;
import com.tny.game.oplog.*;

import java.time.Instant;

/**
 *
 */
public class AbstractLog implements Log {

    /**
     * 日志ID
     */
    private String logID;

    /**
     * 日志
     */
    protected OpLog log;

    /**
     * 玩家日志
     */
    protected UserOpLog userOpLog;

    /**
     * 日志类型
     */
    private String type;

    /**
     * 创建日期
     */
    private int date;

    @SuppressWarnings("unchecked")
    public AbstractLog(String type, String logID, OpLog log, UserOpLog userOpLog) {
        this.type = type;
        this.logID = logID;
        this.log = log;
        this.userOpLog = userOpLog;
        Instant dateTime = log.getCreateAt();
        this.date = DateTimeAide.date2Int(dateTime);
    }

    @Override
    public long getUserId() {
        return this.userOpLog.getUserId();
    }

    @Override
    public String getName() {
        return this.userOpLog.getName();
    }

    @Override
    public long getAt() {
        return this.getLogAt().toEpochMilli();
    }

    @Override
    public int getDate() {
        return this.date;
    }

    @Override
    public int getServerId() {
        return this.userOpLog.getServerId();
    }

    @Override
    public String getPF() {
        return this.userOpLog.getPF();
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public int getLevel() {
        return this.userOpLog.getLevel();
    }

    @Override
    public int getVip() {
        return this.userOpLog.getVip();
    }

    @Override
    public String getOpenId() {
        return this.userOpLog.getOpenId();
    }

    @Override
    public Instant getLogAt() {
        return this.log.getCreateAt();
    }

    @Override
    public String getLogId() {
        return this.logID;
    }

}
