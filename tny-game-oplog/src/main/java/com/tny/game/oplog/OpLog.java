package com.tny.game.oplog;

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
    public abstract long getCreateAt();

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

    protected abstract UserOpLog putUserOpLog(UserOpLog userOpLog);

}