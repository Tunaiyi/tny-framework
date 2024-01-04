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

package com.tny.game.common.scheduler;

import com.tny.game.common.scheduler.cycle.*;

import java.time.Instant;

/**
 * 计时器
 * Created by Kun Yang on 16/4/13.
 */
public interface TimeMeter<C extends TimeCycle> {

    Instant getStartTime();

    Instant getNextTime();

    Instant getPreviousTime();

    Instant getEndTime();

    Instant getSuspendTime();

    long getSpeedMills();

    C getTimeCycle();

    default long countRemainMills() {
        return countRemainMills(System.currentTimeMillis());
    }

    default boolean isSuspend() {
        return this.getNextTime() != null && this.getSuspendTime() != null;
    }

    default boolean isWorking() {
        return this.getNextTime() != null && this.getSuspendTime() == null;
    }

    default boolean isFinish() {
        return this.getNextTime() == null;
    }

    default double getProgress() {
        return getProgress(System.currentTimeMillis());
    }

    default double getTotalProgress() {
        return getTotalProgress(System.currentTimeMillis());
    }

    default double getProgress(long timeMillis) {
        long duration = this.getDuration();
        long progressTime = duration - this.countRemainMills(timeMillis);
        return (double) progressTime / duration;
    }

    default double getTotalProgress(long timeMillis) {
        long duration = this.getTotalDuration();
        long progressTime = duration - this.countEndRemainMills(timeMillis);
        return (double) progressTime / duration;
    }

    default long getTotalDuration() {
        Instant dateTime = this.getStartTime();
        Instant endTime = this.getEndTime();
        if (endTime == null) {
            return -1L;
        }
        return Math.max(endTime.toEpochMilli() - dateTime.toEpochMilli(), 0L);
    }

    default long getDuration() {
        Instant previous = this.getPreviousTime();
        Instant next = this.getNextTime();
        if (previous == null || next == null) {
            return -1;
        }
        return next.toEpochMilli() - previous.toEpochMilli();
    }

    default long countRemainMills(long timeMillis) {
        Instant next = this.getNextTime();
        if (next == null) {
            return -1;
        }
        Instant suspendTime = this.getSuspendTime();
        if (suspendTime != null && suspendTime.toEpochMilli() < timeMillis + this.getSpeedMills()) {
            timeMillis = suspendTime.toEpochMilli();
        }
        return Math.max(next.toEpochMilli() - (timeMillis + this.getSpeedMills()), 0L);
    }

    default long countEndRemainMills() {
        return countEndRemainMills(System.currentTimeMillis());
    }

    default long countEndRemainMills(long timeMillis) {
        Instant next = this.getNextTime();
        if (next == null) {
            return -1;
        }
        Instant end = this.getEndTime();
        if (end == null) {
            return -1;
        }
        Instant suspendTime = this.getSuspendTime();
        if (suspendTime != null && suspendTime.toEpochMilli() <= timeMillis) {
            timeMillis = suspendTime.toEpochMilli();
        }
        return Math.max(end.toEpochMilli() - (timeMillis + this.getSpeedMills()), 0L);
    }

}
