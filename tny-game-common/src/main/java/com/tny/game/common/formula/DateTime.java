package com.tny.game.common.formula;

import java.util.Calendar;
import java.util.Date;

public class DateTime {

    private Calendar calendar;

    public DateTime() {
        this(Calendar.getInstance());
    }

    public DateTime(Calendar calendar) {
        this.calendar = calendar;
    }

    public DateTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        this.calendar = calendar;
    }

    public DateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        this.calendar = calendar;
    }

    public int getYear() {
        return this.calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return this.calendar.get(Calendar.MONTH) + 1;
    }

    public int getDayOfMonth() {
        return this.calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getDayOfWeek() {
        return this.calendar.get(Calendar.DAY_OF_WEEK);
    }

    public int getHours() {
        return this.calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes() {
        return this.calendar.get(Calendar.MINUTE);
    }

    public int getSeconds() {
        return this.calendar.get(Calendar.SECOND);
    }

    public long getTime() {
        return this.calendar.getTimeInMillis();
    }

    public Date getDate() {
        return this.calendar.getTime();
    }

}
