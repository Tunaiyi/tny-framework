package com.tny.game.basics.scheduler;

import com.tny.game.basics.item.*;
import com.tny.game.common.scheduler.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/29 9:09 下午
 */
public abstract class BaseSchedulerBackup extends SchedulerBackup implements Any {

    private long id;

    protected BaseSchedulerBackup() {
    }

    protected BaseSchedulerBackup(TimeTaskScheduler scheduler, long id) {
        super(scheduler);
        this.id = id;
    }

    @Override
    public long getPlayerId() {
        return id;
    }

    @Override
    public long getId() {
        return id;
    }

    protected BaseSchedulerBackup setId(long id) {
        this.id = id;
        return this;
    }

}
