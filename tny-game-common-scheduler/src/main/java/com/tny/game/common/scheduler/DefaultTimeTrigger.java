/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.scheduler;

import com.tny.game.common.scheduler.cycle.*;
import com.tny.game.common.utils.*;

import java.time.Instant;

class DefaultTimeTrigger<C extends TimeCycle> implements TimeTrigger<C> {

    private C timeCycle = null;

    private Instant startTime = null;

    private Instant previousTime = null;

    private Instant nextTime = null;

    private Instant endTime = null;

    private long speedMills = 0;

    private Instant suspendTime = null;

    protected DefaultTimeTrigger(Instant startTime, Instant previousTime, Instant endTime, Instant suspendTime, C timeCycle, long speedMills,
            boolean start) {
        this.startTime = startTime;
        this.timeCycle = timeCycle;
        this.previousTime = previousTime;
        this.endTime = endTime;
        this.speedMills = speedMills;
        this.suspendTime = suspendTime;
        if (start && isCanStart()) {
            this.start();
        }
    }

    @Override
    public Instant getStartTime() {
        return this.startTime;
    }

    @Override
    public Instant getNextTime() {
        return this.nextTime;
    }

    @Override
    public Instant getPreviousTime() {
        return this.previousTime;
    }

    @Override
    public Instant getEndTime() {
        return this.endTime;
    }

    @Override
    public long getSpeedMills() {
        return this.speedMills;
    }

    @Override
    public C getTimeCycle() {
        return this.timeCycle;
    }

    @Override
    public boolean stop() {
        if (this.nextTime != null) {
            this.nextTime = null;
            this.speedMills = 0;
            return true;
        }
        return false;
    }

    public boolean isCanStart() {
        return this.nextTime == null && this.startTime != null && this.timeCycle != null;
    }

    @Override
    public boolean start(C timeCycle, Instant startTime, Instant endTime) {
        if (this.nextTime != null) {
            return false;
        }
        if (startTime == null && this.startTime == null) {
            throw new NullPointerException("startTime is null");
        }
        if (timeCycle == null && this.timeCycle == null) {
            throw new NullPointerException("timeCycle is null");
        }
        if (endTime != null) {
            Instant checkStartTime = startTime != null ? startTime : this.startTime;
            Asserts.checkArgument(endTime.isAfter(checkStartTime));
            this.endTime = endTime;
            this.reset();
        }
        if (timeCycle != null) {
            this.timeCycle = timeCycle;
        }
        if (startTime != null) {
            this.startTime = startTime;
            this.previousTime = startTime;
        }
        if (this.previousTime == null) {
            this.previousTime = this.startTime;
        }
        setup(timeCycle, this.previousTime, false);
        return !this.isFinish();
    }

    private void reset() {
        this.suspendTime = null;
        this.speedMills = 0;
    }

    @Override
    public void restart(C timeCycle, Instant startTime, Instant endTime) {
        if (startTime == null && this.startTime == null) {
            throw new NullPointerException("startTime is null");
        }
        if (timeCycle == null && this.timeCycle == null) {
            throw new NullPointerException("timeCycle is null");
        }
        if (endTime != null) {
            Asserts.checkArgument(endTime.isAfter(startTime));
            this.endTime = endTime;
            this.reset();
        }
        this.startTime = startTime;
        setup(timeCycle, startTime, false);
    }

    private void setup(C timeCycle, Instant time, boolean stop) {
        if (timeCycle != null) {
            this.timeCycle = timeCycle;
        }
        this.previousTime = time;
        if (!stop) {
            this.nextTime = getNextTimeAfter(time);
        }
    }

    @Override
    public boolean trigger(long timeMillis) {
        Instant next = this.nextTime;
        Instant suspend = this.suspendTime;
        Instant checkAt = Instant.ofEpochMilli(timeMillis);
        if (next != null && suspend == null && getCheckMills(timeMillis) >= next.toEpochMilli()) {
            this.previousTime = checkAt.isBefore(next) ? checkAt : next;
            this.nextTime = getNextTimeAfter(next);
            return true;
        }
        return false;
    }

    @Override
    public boolean speedUp(long timeMills) {
        if (!isWorking() || timeMills <= 0) {
            return false;
        }
        this.speedMills = Math.max(this.speedMills + timeMills, 0L);
        return true;
    }

    @Override
    public boolean triggerForce(long timeMillis) {
        Instant next = this.nextTime;
        Instant time = Instant.ofEpochMilli(timeMillis);
        if (next != null) {
            this.previousTime = time.isBefore(next) ? time : next;
            this.nextTime = getNextTimeAfter(next);
            return true;
        }
        return false;
    }

    private Instant getNextTimeAfter(Instant time) {
        if (this.timeCycle == null) {
            return null;
        }
        Instant pot = this.timeCycle.getTimeAfter(time);
        if (this.endTime != null && pot.isAfter(this.endTime)) {
            return null;
        }
        return pot;
    }

    private long getCheckMills(long mills) {
        return this.speedMills + mills;
    }

    @Override
    public Instant getSuspendTime() {
        return this.suspendTime;
    }

    @Override
    public boolean resume(Instant time) {
        Instant nextTime = this.nextTime;
        Instant suspendTime = this.suspendTime;
        if (nextTime == null || suspendTime == null || suspendTime.isAfter(time)) {
            return false;
        }
        long suspendMills = Math.max(time.toEpochMilli() - suspendTime.toEpochMilli(), 0);
        this.nextTime = nextTime.plusMillis((int)suspendMills);
        Instant endTime = this.endTime;
        if (endTime != null) {
            this.endTime = endTime.plusMillis((int)suspendMills);
        }
        this.suspendTime = null;
        return true;
    }

    @Override
    public boolean suspend(Instant time) {
        if (!isWorking() && time.plusMillis((int)this.getSpeedMills())
                .isAfter(this.getNextTime())) {
            return false;
        }
        this.suspendTime = time;
        return true;
    }

    @Override
    public boolean lengthen(long timeMillis) {
        if (isFinish() || timeMillis <= 0) {
            return false;
        }
        Instant nextTime = this.nextTime;
        Instant endTime = this.endTime;
        this.nextTime = nextTime.plusMillis((int)timeMillis);
        if (endTime != null) {
            this.endTime = endTime.plusMillis((int)timeMillis);
        }
        return false;
    }

}
