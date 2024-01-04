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