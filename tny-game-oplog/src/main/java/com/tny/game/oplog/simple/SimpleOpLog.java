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

import com.tny.game.oplog.*;

import java.time.Instant;
import java.util.*;

public class SimpleOpLog extends OpLog {

    private Object protocol;

    private Instant createAt;

    private String threadName;

    private List<UserOpLog> userLogs = new ArrayList<>();

    public SimpleOpLog(Object protocol) {
        super();
        this.protocol = protocol;
        this.createAt = Instant.now();
        this.threadName = Thread.currentThread().getName();
    }

    @Override
    protected UserOpLog putUserOpLog(UserOpLog userOpLog) {
        UserOpLog old = this.getBaseUserOpLog(userOpLog.getUserId());
        if (old != null) {
            return old;
        }
        this.userLogs.add(userOpLog);
        return userOpLog;
    }

    @Override
    public UserOpLog getUserOpLog(long userID) {
        return this.getBaseUserOpLog(userID);
    }

    private UserOpLog getBaseUserOpLog(long userID) {
        for (int index = 0; index < this.userLogs.size(); index++) {
            UserOpLog log = this.userLogs.get(index);
            if (log.getUserId() == userID) {
                return log;
            }
        }
        return null;
    }

    @Override
    public Object getProtocol() {
        return this.protocol;
    }

    @Override
    public Instant getCreateAt() {
        return this.createAt;
    }

    @Override
    public String getThreadName() {
        return this.threadName;
    }

    //	@Override
    //	public int getServerID() {
    //		return this.serverID;
    //	}

    @Override
    public List<UserOpLog> getUserLogs() {
        List<? extends UserOpLog> list = this.userLogs;
        return Collections.unmodifiableList(list);
    }

    //	@Override
    //	protected BaseUserOpLog getBaseUserOpLog(long userID) {
    //		return this.userOpLogMap.get(userID);
    //	}

}
