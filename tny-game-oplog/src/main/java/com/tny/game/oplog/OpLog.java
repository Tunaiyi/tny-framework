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

import java.time.Instant;
import java.util.List;

public abstract class OpLog {

    /**
     * 获取指定ID用户日志
     *
     * @param userID
     * @return
     */
    public abstract UserOpLog getUserOpLog(long userID);

    /**
     * 获取日志产生的操作名
     *
     * @return
     */
    public abstract Object getProtocol();

    /**
     * 日志创建时间
     *
     * @return
     */
    public abstract Instant getCreateAt();

    /**
     * 创建线程名字
     *
     * @return
     */
    public abstract String getThreadName();

    /**
     * 用户日志 map
     *
     * @return
     */
    public abstract List<UserOpLog> getUserLogs();

    /**
     * 插入用户日志
     *
     * @param userOpLog 用户
     * @return
     */
    protected abstract UserOpLog putUserOpLog(UserOpLog userOpLog);

}