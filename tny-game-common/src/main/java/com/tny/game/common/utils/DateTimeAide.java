package com.tny.game.common.utils;

import org.joda.time.*;
import org.joda.time.format.*;

import java.text.ParseException;

public class DateTimeAide {

    private DateTimeAide() {
    }

    /**
     * HH:mm
     */
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("HH:mm");

    /**
     * yyyy-MM-dd
     */
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * yyyy-MM-dd HH:mm
     */
    public static final DateTimeFormatter DATE_TIME_MIN_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    /**
     * yyyyMMddHHmmss
     */
    public static final DateTimeFormatter DATE_TIME_2_NUM_FORMAT = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    /**
     * MM/dd/yyyy
     */
    public static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormat.forPattern("MM/dd/yyyy");

    public static DateTime date(String date) throws ParseException {
        return DATE_FORMAT.parseDateTime(date);
    }

    public static DateTime date(String date, DateTimeFormatter formatter) throws ParseException {
        return DATE_FORMAT.parseDateTime(date);
    }

    public static DateTime dateTime(String date) throws ParseException {
        return DATE_TIME_FORMAT.parseDateTime(date);
    }

    public static DateTime dateTime(String date, DateTimeFormatter formatter) throws ParseException {
        return formatter.parseDateTime(date);
    }

    public static DateTime now() {
        return DateTime.now();
    }

    public static LocalDate today() {
        DateRange range = NOW_RANGE;
        if (range.isSameDate())
            return range.date;
        NOW_RANGE = range = new DateRange();
        return range.date;
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

    public static long time2DateLong(DateTime now) {
        return ((((100L * now.getYear()) +
                  now.getMonthOfYear()) * 100) +
                now.getDayOfMonth());
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

    public static DateTime millisLong2Time(long timeInt) {
        int millis = (int) (timeInt % 1000);
        int seconds = (int) ((timeInt = timeInt / 1000) % 100);
        int minute = (int) ((timeInt = timeInt / 100) % 100);
        int hour = (int) ((timeInt = timeInt / 100) % 100);
        int day = (int) ((timeInt = timeInt / 100) % 100);
        int month = (int) ((timeInt = timeInt / 100) % 100);
        int year = (int) (timeInt / 100);
        return new DateTime(year, month, day, hour, minute, seconds, millis);
    }

    public static long time2Second(DateTime now) {
        return (((((((((100L * now.getYear()) +
                       now.getMonthOfYear()) * 100) +
                     now.getDayOfMonth()) * 100) +
                   now.getHourOfDay()) * 100) +
                 now.getMinuteOfHour()) * 100) +
               now.getSecondOfMinute();
    }

    public static long time2Minute(DateTime now) {
        return ((((((((100L * now.getYear()) +
                      now.getMonthOfYear()) * 100) +
                    now.getDayOfMonth()) * 100) +
                  now.getHourOfDay()) * 100) +
                now.getMinuteOfHour());
    }

    public static DateTime second2Time(long timeInt) {
        int seconds = (int) (timeInt % 100);
        int minute = (int) ((timeInt = timeInt / 100) % 100);
        int hour = (int) ((timeInt = timeInt / 100) % 100);
        int day = (int) ((timeInt = timeInt / 100) % 100);
        int month = (int) ((timeInt = timeInt / 100) % 100);
        int year = (int) (timeInt / 100);
        return new DateTime(year, month, day, hour, minute, seconds);
    }

    private static class DateRange {
        LocalDate date;
        long start;
        long end;

        public DateRange() {
            this.date = LocalDate.now();
            DateTime dateTime = new DateTime(this.date.getYear(), this.date.getMonthOfYear(), this.date.getDayOfMonth(), 0, 0, 0, 0);
            this.start = dateTime.getMillis();
            this.end = dateTime.plusDays(1).getMillis();
        }

        public boolean isSameDate() {
            long time = System.currentTimeMillis();
            return this.start <= time && time < this.end;
        }

    }

    private static volatile DateRange NOW_RANGE = new DateRange();

}
