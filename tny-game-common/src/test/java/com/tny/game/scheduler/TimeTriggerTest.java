package com.tny.game.scheduler;

import com.tny.game.scheduler.cycle.DurationTimeCycle;
import com.tny.game.scheduler.cycle.TimeCycle;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * TimeTrigger 测试
 * Created by Kun Yang on 16/2/22.
 */
public class TimeTriggerTest {

    private long at = System.currentTimeMillis();
    private long restartAt = at + 1000;

    private DateTime atTime = new DateTime(at);
    private DateTime restartAtTime = new DateTime(restartAt);
    private DateTime endTime = new DateTime(at + 3010);

    private DurationTimeCycle CYCLE_1000 = DurationTimeCycle.of(1000);
    private DurationTimeCycle CYCLE_2000 = DurationTimeCycle.of(2000);

    private TimeTrigger<TimeCycle> startNoEndTrigger = TimeTriggerBuilder.newBuilder()
            .setTimeCycle(CYCLE_1000)
            .setPreviousTime(new DateTime(at))
            .build();

    private TimeTrigger<TimeCycle> stopNoEndTrigger = TimeTriggerBuilder.newBuilder()
            .setTimeCycle(CYCLE_1000)
            .setPreviousTime(new DateTime(at))
            .setStop()
            .build();

    private TimeTrigger<TimeCycle> startEndTrigger = TimeTriggerBuilder.newBuilder()
            .setTimeCycle(CYCLE_1000)
            .setPreviousTime(new DateTime(at))
            .setEndTime(endTime)
            .build();

    @Before
    public void setUp() throws Exception {

        startNoEndTrigger = TimeTriggerBuilder.newBuilder()
                .setTimeCycle(CYCLE_1000)
                .setPreviousTime(new DateTime(at))
                .build();

        stopNoEndTrigger = TimeTriggerBuilder.newBuilder()
                .setTimeCycle(CYCLE_1000)
                .setPreviousTime(new DateTime(at))
                .setStop()
                .build();

        startEndTrigger = TimeTriggerBuilder.newBuilder()
                .setTimeCycle(CYCLE_1000)
                .setPreviousTime(new DateTime(at))
                .setEndTime(endTime)
                .build();

    }

    @Test
    public void getNextTime() throws Exception {
        Assert.assertEquals(at + CYCLE_1000.getDuration().getMillis(), startNoEndTrigger.getNextTime().getMillis());
        Assert.assertNull(stopNoEndTrigger.getNextTime());
    }

    @Test
    public void getPreviousTime() throws Exception {
        Assert.assertEquals(at, startNoEndTrigger.getPreviousTime().getMillis());
        Assert.assertEquals(at, stopNoEndTrigger.getPreviousTime().getMillis());
    }

    @Test
    public void stop() throws Exception {
        Assert.assertTrue(stopNoEndTrigger.isStop());
        startNoEndTrigger.stop();
        Assert.assertTrue(startNoEndTrigger.isStop());
    }

    @Test
    public void restartAt() throws Exception {
        if (!startNoEndTrigger.isStop())
            startNoEndTrigger.stop();
        startNoEndTrigger.restartAt(restartAt);
        Assert.assertEquals(restartAtTime.plus(CYCLE_1000.getDuration()), startNoEndTrigger.getNextTime());

        startNoEndTrigger.restartAt(CYCLE_2000, restartAt);
        Assert.assertEquals(restartAtTime.plus(CYCLE_2000.getDuration()), startNoEndTrigger.getNextTime());
    }

    @Test
    public void trigger() throws Exception {
        Assert.assertFalse(startNoEndTrigger.trigger());
        Assert.assertTrue(startNoEndTrigger.trigger(restartAt));
        Assert.assertEquals(atTime
                        .plus(CYCLE_1000.getDuration())
                        .plus(CYCLE_1000.getDuration()),
                startNoEndTrigger.getNextTime());

        Assert.assertFalse(startEndTrigger.trigger());
        Assert.assertTrue(startEndTrigger.trigger(endTime.getMillis() + 100));
        DateTime exp = atTime.plus(CYCLE_1000.getDuration())
                .plus(CYCLE_1000.getDuration());
        Assert.assertEquals(exp, startEndTrigger.getNextTime());

        Assert.assertTrue(startEndTrigger.trigger(endTime.getMillis() + 100));
        exp = exp.plus(CYCLE_1000.getDuration());
        Assert.assertEquals(exp, startEndTrigger.getNextTime());

        Assert.assertTrue(startEndTrigger.trigger(endTime.getMillis() + 100));
        Assert.assertNull(startEndTrigger.getNextTime());

        Assert.assertFalse(startEndTrigger.trigger(endTime.getMillis() + 100));
    }


    @Test
    public void triggerForce() throws Exception {
        Assert.assertTrue(startNoEndTrigger.triggerForce());
        Assert.assertEquals(atTime
                        .plus(CYCLE_1000.getDuration())
                        .plus(CYCLE_1000.getDuration()),
                startNoEndTrigger.getNextTime());

        Assert.assertTrue(startEndTrigger.triggerForce());
        DateTime exp = atTime.plus(CYCLE_1000.getDuration())
                .plus(CYCLE_1000.getDuration());
        Assert.assertEquals(exp, startEndTrigger.getNextTime());

        Assert.assertTrue(startEndTrigger.triggerForce());
        exp = exp.plus(CYCLE_1000.getDuration());
        Assert.assertEquals(exp, startEndTrigger.getNextTime());

        Assert.assertTrue(startEndTrigger.triggerForce());
        Assert.assertNull(startEndTrigger.getNextTime());

        Assert.assertFalse(startEndTrigger.triggerForce());
    }

    @Test
    public void countRemainMills() throws Exception {
        long cost = 300;
        long duration = CYCLE_1000.getDuration().getMillis();
        Assert.assertEquals(duration - cost, startNoEndTrigger.countRemainMills(at + cost));
        Assert.assertEquals(0, startNoEndTrigger.countRemainMills(at + duration));
        Assert.assertEquals(0, startNoEndTrigger.countRemainMills(at + duration + 100));
        startNoEndTrigger.stop();
        Assert.assertEquals(-1, startNoEndTrigger.countRemainMills(at + duration + 100));
    }

}