package com.tny.game.common.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;

public class DateTimeHelper {

    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("HH:mm");
    public static final DateTimeFormatter SIMPLE_TIME_FORMAT = DateTimeFormat.forPattern("H:mm");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_2_NUMBER_FORMAT = DateTimeFormat.forPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter SIMPLE_DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");


    public static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormat.forPattern("MM/dd/yyyy");
    public static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormat.forPattern("HH:mm");

    public static DateTime date(String date) throws ParseException {
        return DATE_TIME_FORMAT.parseDateTime(date);
    }

    public static DateTime date(String date, DateTimeFormatter fomratter) throws ParseException {
        return fomratter.parseDateTime(date);
    }

    private DateTimeHelper() {
    }

    private static class DateRange {
        LocalDate date;
        long start;
        long end;

        public DateRange() {
            this.date = LocalDate.now();
            DateTime dateTime = new DateTime(this.date.getYear(), this.date.getMonthOfYear(), this.date.getDayOfMonth(), 0, 0);
            this.start = dateTime.getMillis();
            this.end = dateTime.plusDays(1).getMillis();
        }

        public boolean isSameDate() {
            long time = System.currentTimeMillis();
            return this.start <= time && time < this.end;
        }

    }

    private static volatile DateRange NOW_RANGE = new DateRange();

    public static LocalDate now() {
        DateRange ranage = NOW_RANGE;
        if (ranage.isSameDate())
            return ranage.date;
        NOW_RANGE = ranage = new DateRange();
        return ranage.date;
    }

    public static LocalDate int2Date(int dateInt) {
        return new LocalDate(dateInt / 10000, (dateInt / 100) % 100, dateInt % 100);
    }

    public static int date2Int(LocalDate now) {
        return (((now.getYear() * 100) + now.getMonthOfYear()) * 100) + now.getDayOfMonth();
    }

    public static int date2Int(DateTime now) {
        return (((now.getYear() * 100) + now.getMonthOfYear()) * 100) + now.getDayOfMonth();
    }

    public static long time2MillisLong(DateTime now) {
        return ((((((((((100L * now.getYear()) +
                now.getMonthOfYear()) * 100) +
                now.getDayOfMonth()) * 100) +
                now.getHourOfDay()) * 100) +
                now.getMinuteOfHour()) * 100) +
                now.getSecondOfMinute()) * 1000 +
                now.getMillisOfSecond();
    }

    public static long time2DateLong(DateTime now) {
        return ((((100L * now.getYear()) +
                now.getMonthOfYear()) * 100) +
                now.getDayOfMonth());
    }

    public static DateTime millisLong2Time(long timeInt) {
        int millis = (int) (timeInt % 1000);
        int sercond = (int) ((timeInt = timeInt / 1000) % 100);
        int minute = (int) ((timeInt = timeInt / 100) % 100);
        int hour = (int) ((timeInt = timeInt / 100) % 100);
        int day = (int) ((timeInt = timeInt / 100) % 100);
        int month = (int) ((timeInt = timeInt / 100) % 100);
        int year = (int) (timeInt / 100);
        return new DateTime(year, month, day, hour, minute, sercond, millis);
    }

    public static long time2SecondLong(DateTime now) {
        return (((((((((100L * now.getYear()) +
                now.getMonthOfYear()) * 100) +
                now.getDayOfMonth()) * 100) +
                now.getHourOfDay()) * 100) +
                now.getMinuteOfHour()) * 100) +
                now.getSecondOfMinute();
    }

    public static DateTime secondLong2Time(long timeInt) {
        int sercond = (int) (timeInt % 100);
        int minute = (int) ((timeInt = timeInt / 100) % 100);
        int hour = (int) ((timeInt = timeInt / 100) % 100);
        int day = (int) ((timeInt = timeInt / 100) % 100);
        int month = (int) ((timeInt = timeInt / 100) % 100);
        int year = (int) (timeInt / 100);
        return new DateTime(year, month, day, hour, minute, sercond);
    }

    public static void main(String[] args) {
        DateTime now = new DateTime();
        long value = time2SecondLong(now);
        DateTime time = secondLong2Time(value);
        System.out.println(now);
        System.out.println(value);
        System.out.println(time);
    }

}
