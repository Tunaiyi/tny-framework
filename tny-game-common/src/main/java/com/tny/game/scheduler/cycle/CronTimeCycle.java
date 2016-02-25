package com.tny.game.scheduler.cycle;

import org.joda.time.DateTime;
import org.quartz.CronExpression;

import java.text.ParseException;

/**
 * Created by Kun Yang on 16/2/20.
 */
public class CronTimeCycle implements TimeCycle {

    private CronExpression expression;

    private CronTimeCycle(CronExpression expression) {
        this.expression = expression;
    }

    public static final CronTimeCycle of(String expr) throws ParseException {
        return new CronTimeCycle(new CronExpression(expr));
    }


    public static final CronTimeCycle of(CronExpression expr) throws ParseException {
        return new CronTimeCycle(expr);
    }

    @Override
    public DateTime getTimeAfter(DateTime dateTime) {
        return new DateTime(expression.getTimeAfter(dateTime.toDate()));
    }

}
