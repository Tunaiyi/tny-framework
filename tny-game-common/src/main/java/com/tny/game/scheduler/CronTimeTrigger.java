package com.tny.game.scheduler;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class CronTimeTrigger implements TimeTrigger {

    private String timeExpression;

    private AbstractTrigger<CronTrigger> trigger;

    public CronTimeTrigger(String timeExpression) throws ParseException {
        this(System.currentTimeMillis(), timeExpression);
    }

    @SuppressWarnings("unchecked")
    public CronTimeTrigger(long startTimeMillis, String timeExpression) throws ParseException {
        this.timeExpression = timeExpression;
        Date start = startTimeMillis > 0 ? new Date(startTimeMillis) : new Date();
        this.trigger = (AbstractTrigger<CronTrigger>) TriggerBuilder.newTrigger()
                .startAt(start)
                .withSchedule(CronScheduleBuilder.cronSchedule(timeExpression))
                .build();
        this.setNext(start.getTime());
    }

    @Override
    public boolean trigger() {
        return this.trigger(System.currentTimeMillis());
    }

    public void setNext(long time) {
        CronTriggerImpl cronTrigger = (CronTriggerImpl) this.trigger;
        cronTrigger.setNextFireTime(new Date(time));
        cronTrigger.triggered(null);
    }

    @Override
    public boolean trigger(long time) {
        long fireTime = this.trigger.getNextFireTime().getTime();
        if (time > fireTime) {
            synchronized (this) {
                boolean result = false;
                if (time > fireTime) {
                    this.trigger.triggered(null);
                    result = true;
                }
                return result;
            }
        }
        return false;
    }

    @Override
    public long getLastFireTime() {
        //		return this.lastFireTime;
        return this.trigger.getPreviousFireTime().getTime();
    }

    @Override
    public long getTriggerTime() {
        return this.trigger.getNextFireTime().getTime();
    }

    public String getTimeExpression() {
        return this.timeExpression;
    }

    public static void main(String[] args) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2015);
        cal.set(Calendar.MONTH, 7);
        cal.set(Calendar.DAY_OF_MONTH, Calendar.MAY);
        cal.set(Calendar.HOUR_OF_DAY, 20);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long time = cal.getTimeInMillis();
        System.out.println(new Date(time));
        CronTimeTrigger timeTrigger = new CronTimeTrigger(time - 1000, "0 */4 * * * ?");
        //		CronTimeTrigger timeTrigger = new CronTimeTrigger(1367841600000L, "0 0 20 ? * 2");
        System.out.println(new Date(timeTrigger.getLastFireTime()));
        System.out.println(new Date(timeTrigger.getTriggerTime()));
        timeTrigger.trigger();
        System.out.println(new Date(timeTrigger.getLastFireTime()));
        System.out.println(new Date(timeTrigger.getTriggerTime()));
        timeTrigger.trigger();
        System.out.println(new Date(timeTrigger.getLastFireTime()));
        System.out.println(new Date(timeTrigger.getTriggerTime()));
        timeTrigger.trigger();
        System.out.println(new Date(timeTrigger.getLastFireTime()));
        System.out.println(new Date(timeTrigger.getTriggerTime()));
    }

}
