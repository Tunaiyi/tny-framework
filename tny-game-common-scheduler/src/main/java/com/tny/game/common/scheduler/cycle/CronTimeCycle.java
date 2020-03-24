package com.tny.game.common.scheduler.cycle;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

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

    public CronExpression getExpression() {
        return this.expression;
    }

    @Override
    public Instant getTimeAfter(Instant instant) {
        return Instant.ofEpochMilli(this.expression.getTimeAfter(new Date(instant.toEpochMilli())).getTime());
    }
}
