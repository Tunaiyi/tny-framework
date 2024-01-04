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

package com.tny.game.scheduler;

import com.tny.game.common.scheduler.*;
import com.tny.game.common.scheduler.cycle.*;
import org.junit.jupiter.api.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TimeTrigger 测试
 * Created by Kun Yang on 16/2/22.
 */
public class TimeTriggerTest {

    private long at = System.currentTimeMillis();

    private long restartAt = this.at + 1000;

    private Instant atTime = Instant.ofEpochMilli(this.at);

    private Instant restartAtTime = Instant.ofEpochMilli(this.restartAt);

    private Instant endTime = Instant.ofEpochMilli(this.at + 3010);

    private DurationTimeCycle CYCLE_1000 = DurationTimeCycle.of(1000);

    private DurationTimeCycle CYCLE_2000 = DurationTimeCycle.of(2000);

    private TimeTrigger<TimeCycle> startNoEndTrigger = TimeTriggerBuilder.newBuilder()
            .setTimeCycle(this.CYCLE_1000)
            .setStartTime(this.atTime)
            .buildStart();

    private TimeTrigger<TimeCycle> stopNoEndTrigger = TimeTriggerBuilder.newBuilder()
            .setTimeCycle(this.CYCLE_1000)
            .setStartTime(this.atTime)
            .buildStop();

    private TimeTrigger<TimeCycle> startEndTrigger = TimeTriggerBuilder.newBuilder()
            .setTimeCycle(this.CYCLE_1000)
            .setStartTime(this.atTime)
            .setEndTime(this.endTime)
            .buildStart();

    @BeforeEach
    public void setUp() throws Exception {

        TimeTrigger<TimeCycle> startNoEndTrigger = TimeTriggerBuilder.newBuilder()
                .setTimeCycle(this.CYCLE_1000)
                .setStartTime(this.atTime)
                .buildStart();

        TimeTrigger<TimeCycle> stopNoEndTrigger = TimeTriggerBuilder.newBuilder()
                .setTimeCycle(this.CYCLE_1000)
                .setStartTime(this.atTime)
                .buildStop();

        TimeTrigger<TimeCycle> startEndTrigger = TimeTriggerBuilder.newBuilder()
                .setTimeCycle(this.CYCLE_1000)
                .setStartTime(this.atTime)
                .setEndTime(this.endTime)
                .buildStart();

    }

    @Test
    public void getNextTime() throws Exception {
        assertEquals(this.at + this.CYCLE_1000.getDuration().toMillis(), this.startNoEndTrigger.getNextTime().toEpochMilli());
        assertNull(this.stopNoEndTrigger.getNextTime());
        assertEquals(this.atTime, this.stopNoEndTrigger.getStartTime());
        assertEquals(this.atTime, this.startNoEndTrigger.getStartTime());
    }

    @Test
    public void getPreviousTime() throws Exception {
        assertEquals(this.at, this.startNoEndTrigger.getPreviousTime().toEpochMilli());
        assertNull(this.stopNoEndTrigger.getPreviousTime());
    }

    @Test
    public void stop() throws Exception {
        assertTrue(this.stopNoEndTrigger.isFinish());
        this.startNoEndTrigger.stop();
        assertTrue(this.startNoEndTrigger.isFinish());
    }

    @Test
    public void restartAt() throws Exception {
        if (!this.startNoEndTrigger.isFinish()) {
            this.startNoEndTrigger.stop();
        }
        this.startNoEndTrigger.restart(this.restartAt);
        assertEquals(this.restartAtTime.plus(this.CYCLE_1000.getDuration()), this.startNoEndTrigger.getNextTime());

        this.startNoEndTrigger.restart(this.CYCLE_2000, this.restartAt);
        assertEquals(this.restartAtTime.plus(this.CYCLE_2000.getDuration()), this.startNoEndTrigger.getNextTime());
    }

    @Test
    public void trigger() throws Exception {
        assertFalse(this.startNoEndTrigger.trigger());
        assertTrue(this.startNoEndTrigger.trigger(this.restartAt));
        assertEquals(this.atTime
                        .plus(this.CYCLE_1000.getDuration())
                        .plus(this.CYCLE_1000.getDuration()),
                this.startNoEndTrigger.getNextTime());

        assertFalse(this.startEndTrigger.trigger());
        assertTrue(this.startEndTrigger.trigger(this.endTime.toEpochMilli() + 100));
        Instant exp = this.atTime.plus(this.CYCLE_1000.getDuration())
                .plus(this.CYCLE_1000.getDuration());
        assertEquals(exp, this.startEndTrigger.getNextTime());

        assertTrue(this.startEndTrigger.trigger(this.endTime.toEpochMilli() + 100));
        exp = exp.plus(this.CYCLE_1000.getDuration());
        assertEquals(exp, this.startEndTrigger.getNextTime());

        assertTrue(this.startEndTrigger.trigger(this.endTime.toEpochMilli() + 100));
        assertNull(this.startEndTrigger.getNextTime());

        assertFalse(this.startEndTrigger.trigger(this.endTime.toEpochMilli() + 100));
    }

    @Test
    public void triggerForce() throws Exception {
        Instant nowDate = Instant.now();
        long now = nowDate.toEpochMilli();
        assertTrue(this.startNoEndTrigger.triggerForce(now));
        assertEquals(this.atTime
                        .plus(this.CYCLE_1000.getDuration())
                        .plus(this.CYCLE_1000.getDuration()),
                this.startNoEndTrigger.getNextTime());

        assertTrue(this.startEndTrigger.triggerForce(now));
        Instant exp = this.atTime.plus(this.CYCLE_1000.getDuration())
                .plus(this.CYCLE_1000.getDuration());
        assertEquals(exp, this.startEndTrigger.getNextTime());

        assertTrue(this.startEndTrigger.triggerForce(now));
        exp = exp.plus(this.CYCLE_1000.getDuration());
        assertEquals(exp, this.startEndTrigger.getNextTime());

        assertTrue(this.startEndTrigger.triggerForce(now));
        assertNull(this.startEndTrigger.getNextTime());

        assertFalse(this.startEndTrigger.triggerForce(now));
    }

    @Test
    public void countRemainMills() throws Exception {
        long cost = 300;
        long duration = this.CYCLE_1000.getDuration().toMillis();
        assertEquals(duration - cost, this.startNoEndTrigger.countRemainMills(this.at + cost));
        assertEquals(0, this.startNoEndTrigger.countRemainMills(this.at + duration));
        assertEquals(0, this.startNoEndTrigger.countRemainMills(this.at + duration + 100));
        this.startNoEndTrigger.stop();
        assertEquals(-1, this.startNoEndTrigger.countRemainMills(this.at + duration + 100));
    }

    @Test
    public void resume() throws Exception {
    }

    @Test
    public void suspend() throws Exception {
        Instant checkAt;
        long duration = (long) this.CYCLE_1000.getDuration().toMillis();
        Instant suspendTime = this.atTime.plusMillis(500);
        assertTrue(this.startNoEndTrigger.suspend(suspendTime));
        assertTrue(this.startNoEndTrigger.isSuspend());
        assertFalse(this.startNoEndTrigger.isWorking());

        long cost = 100;
        checkAt = this.atTime.plusMillis(100);
        assertEquals(duration - cost, this.startNoEndTrigger.countRemainMills(checkAt.toEpochMilli()));

        cost = 500;
        checkAt = this.atTime.plusMillis(30000);
        assertEquals(duration - cost, this.startNoEndTrigger.countRemainMills(checkAt.toEpochMilli()));

        Instant resumeTime = this.atTime.plusMillis(200);
        assertFalse(this.startNoEndTrigger.resume(resumeTime));
        assertTrue(this.startNoEndTrigger.isSuspend());
        assertFalse(this.startNoEndTrigger.isWorking());

        resumeTime = this.atTime.plusMillis(3000);
        assertTrue(this.startNoEndTrigger.resume(resumeTime));
        assertFalse(this.startNoEndTrigger.isSuspend());
        assertTrue(this.startNoEndTrigger.isWorking());

        assertEquals(cost + 100, this.startNoEndTrigger.countRemainMills(resumeTime.plusMillis(-100).toEpochMilli()));
        assertEquals(duration - cost, this.startNoEndTrigger.countRemainMills(resumeTime.toEpochMilli()));
        resumeTime = this.atTime.plusMillis(3200);
        assertEquals(duration - cost - 200, this.startNoEndTrigger.countRemainMills(resumeTime.toEpochMilli()));
    }

    @Test
    public void lengthen() throws Exception {
        long lengthenTime = 2000;
        long duration = (long) this.CYCLE_1000.getDuration().toMillis();
        this.startNoEndTrigger.lengthen(lengthenTime);
        Instant checkAt = this.atTime;
        assertEquals(duration + 2000, this.startNoEndTrigger.countRemainMills(checkAt.toEpochMilli()));
    }

}