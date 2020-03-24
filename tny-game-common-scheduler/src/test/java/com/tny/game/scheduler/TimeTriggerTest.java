package com.tny.game.scheduler;

import com.tny.game.common.scheduler.*;
import com.tny.game.common.scheduler.cycle.*;
import org.junit.*;

import java.time.Instant;

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

    @Before
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
        Assert.assertEquals(this.at + this.CYCLE_1000.getDuration().toMillis(), this.startNoEndTrigger.getNextTime().toEpochMilli());
        Assert.assertNull(this.stopNoEndTrigger.getNextTime());
        Assert.assertEquals(this.atTime, this.stopNoEndTrigger.getStartTime());
        Assert.assertEquals(this.atTime, this.startNoEndTrigger.getStartTime());
    }

    @Test
    public void getPreviousTime() throws Exception {
        Assert.assertEquals(this.at, this.startNoEndTrigger.getPreviousTime().toEpochMilli());
        Assert.assertNull(this.stopNoEndTrigger.getPreviousTime());
    }

    @Test
    public void stop() throws Exception {
        Assert.assertTrue(this.stopNoEndTrigger.isFinish());
        this.startNoEndTrigger.stop();
        Assert.assertTrue(this.startNoEndTrigger.isFinish());
    }

    @Test
    public void restartAt() throws Exception {
        if (!this.startNoEndTrigger.isFinish())
            this.startNoEndTrigger.stop();
        this.startNoEndTrigger.restart(this.restartAt);
        Assert.assertEquals(this.restartAtTime.plus(this.CYCLE_1000.getDuration()), this.startNoEndTrigger.getNextTime());

        this.startNoEndTrigger.restart(this.CYCLE_2000, this.restartAt);
        Assert.assertEquals(this.restartAtTime.plus(this.CYCLE_2000.getDuration()), this.startNoEndTrigger.getNextTime());
    }

    @Test
    public void trigger() throws Exception {
        Assert.assertFalse(this.startNoEndTrigger.trigger());
        Assert.assertTrue(this.startNoEndTrigger.trigger(this.restartAt));
        Assert.assertEquals(this.atTime
                        .plus(this.CYCLE_1000.getDuration())
                        .plus(this.CYCLE_1000.getDuration()),
                this.startNoEndTrigger.getNextTime());

        Assert.assertFalse(this.startEndTrigger.trigger());
        Assert.assertTrue(this.startEndTrigger.trigger(this.endTime.toEpochMilli() + 100));
        Instant exp = this.atTime.plus(this.CYCLE_1000.getDuration())
                                 .plus(this.CYCLE_1000.getDuration());
        Assert.assertEquals(exp, this.startEndTrigger.getNextTime());

        Assert.assertTrue(this.startEndTrigger.trigger(this.endTime.toEpochMilli() + 100));
        exp = exp.plus(this.CYCLE_1000.getDuration());
        Assert.assertEquals(exp, this.startEndTrigger.getNextTime());

        Assert.assertTrue(this.startEndTrigger.trigger(this.endTime.toEpochMilli() + 100));
        Assert.assertNull(this.startEndTrigger.getNextTime());

        Assert.assertFalse(this.startEndTrigger.trigger(this.endTime.toEpochMilli() + 100));
    }


    @Test
    public void triggerForce() throws Exception {
        Instant nowDate = Instant.now();
        long now = nowDate.toEpochMilli();
        Assert.assertTrue(this.startNoEndTrigger.triggerForce(now));
        Assert.assertEquals(this.atTime
                        .plus(this.CYCLE_1000.getDuration())
                        .plus(this.CYCLE_1000.getDuration()),
                this.startNoEndTrigger.getNextTime());

        Assert.assertTrue(this.startEndTrigger.triggerForce(now));
        Instant exp = this.atTime.plus(this.CYCLE_1000.getDuration())
                                 .plus(this.CYCLE_1000.getDuration());
        Assert.assertEquals(exp, this.startEndTrigger.getNextTime());

        Assert.assertTrue(this.startEndTrigger.triggerForce(now));
        exp = exp.plus(this.CYCLE_1000.getDuration());
        Assert.assertEquals(exp, this.startEndTrigger.getNextTime());

        Assert.assertTrue(this.startEndTrigger.triggerForce(now));
        Assert.assertNull(this.startEndTrigger.getNextTime());

        Assert.assertFalse(this.startEndTrigger.triggerForce(now));
    }

    @Test
    public void countRemainMills() throws Exception {
        long cost = 300;
        long duration = this.CYCLE_1000.getDuration().toMillis();
        Assert.assertEquals(duration - cost, this.startNoEndTrigger.countRemainMills(this.at + cost));
        Assert.assertEquals(0, this.startNoEndTrigger.countRemainMills(this.at + duration));
        Assert.assertEquals(0, this.startNoEndTrigger.countRemainMills(this.at + duration + 100));
        this.startNoEndTrigger.stop();
        Assert.assertEquals(-1, this.startNoEndTrigger.countRemainMills(this.at + duration + 100));
    }

    @Test
    public void resume() throws Exception {
    }

    @Test
    public void suspend() throws Exception {
        Instant checkAt;
        long duration = (long) this.CYCLE_1000.getDuration().toMillis();
        Instant suspendTime = this.atTime.plusMillis(500);
        Assert.assertTrue(this.startNoEndTrigger.suspend(suspendTime));
        Assert.assertTrue(this.startNoEndTrigger.isSuspend());
        Assert.assertFalse(this.startNoEndTrigger.isWorking());

        long cost = 100;
        checkAt = this.atTime.plusMillis(100);
        Assert.assertEquals(duration - cost, this.startNoEndTrigger.countRemainMills(checkAt.toEpochMilli()));

        cost = 500;
        checkAt = this.atTime.plusMillis(30000);
        Assert.assertEquals(duration - cost, this.startNoEndTrigger.countRemainMills(checkAt.toEpochMilli()));


        Instant resumeTime = this.atTime.plusMillis(200);
        Assert.assertFalse(this.startNoEndTrigger.resume(resumeTime));
        Assert.assertTrue(this.startNoEndTrigger.isSuspend());
        Assert.assertFalse(this.startNoEndTrigger.isWorking());

        resumeTime = this.atTime.plusMillis(3000);
        Assert.assertTrue(this.startNoEndTrigger.resume(resumeTime));
        Assert.assertFalse(this.startNoEndTrigger.isSuspend());
        Assert.assertTrue(this.startNoEndTrigger.isWorking());

        Assert.assertEquals(cost + 100, this.startNoEndTrigger.countRemainMills(resumeTime.plusMillis(-100).toEpochMilli()));
        Assert.assertEquals(duration - cost, this.startNoEndTrigger.countRemainMills(resumeTime.toEpochMilli()));
        resumeTime = this.atTime.plusMillis(3200);
        Assert.assertEquals(duration - cost - 200, this.startNoEndTrigger.countRemainMills(resumeTime.toEpochMilli()));
    }

    @Test
    public void lengthen() throws Exception {
        long lengthenTime = 2000;
        long duration = (long) this.CYCLE_1000.getDuration().toMillis();
        this.startNoEndTrigger.lengthen(lengthenTime);
        Instant checkAt = this.atTime;
        Assert.assertEquals(duration + 2000, this.startNoEndTrigger.countRemainMills(checkAt.toEpochMilli()));
    }

}