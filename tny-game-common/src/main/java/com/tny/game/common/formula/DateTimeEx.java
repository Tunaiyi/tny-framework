package com.tny.game.common.formula;

import com.tny.game.common.utils.DateTimeAide;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;

public class DateTimeEx {

    public static DateTime date(String date) throws ParseException {
        return DateTimeAide.DATE_FORMAT.parseDateTime(date);
    }

    public static DateTime date(String date, DateTimeFormatter formatter) throws ParseException {
        return DateTimeAide.DATE_FORMAT.parseDateTime(date);
    }

    public static DateTime dateTime(String date) throws ParseException {
        return DateTimeAide.DATE_TIME_FORMAT.parseDateTime(date);
    }

    public static DateTime dateTime(String date, DateTimeFormatter formatter) throws ParseException {
        return formatter.parseDateTime(date);
    }

    public static DateTime now() {
        return DateTime.now();
    }

    public static DateTime time(DateTime time, int hour) {
        return time(time, hour, 0);
    }

    public static DateTime time(DateTime time, int hour, int minutes) {
        return time(time, hour, minutes, 0);
    }

    public static DateTime time(DateTime time, int hour, int minutes, int seconds) {
        return time(time, hour, minutes, seconds, 0);
    }

    public static DateTime time(DateTime time, int hour, int minutes, int seconds, int millis) {
        return time.withTime(new LocalTime(hour, minutes, seconds, millis));
    }

    public static LocalDate today() {
        return DateTimeAide.today();
    }

    public static DateTime today(int hour) {
        return today(hour, 0);
    }

    public static DateTime today(int hour, int minutes) {
        return today(hour, minutes, 0);
    }

    public static DateTime today(int hour, int minutes, int seconds) {
        return today(hour, minutes, seconds, 0);
    }

    public static DateTime today(int hour, int minutes, int seconds, int millis) {
        return today().toDateTime(new LocalTime(hour, minutes, seconds, millis));
    }

    public static DateTime ofWeek(int week) {
        return ofWeek(week, 0);
    }

    public static DateTime ofWeek(int week, int hour) {
        return ofWeek(week, hour, 0);
    }

    public static DateTime ofWeek(int week, int hour, int minutes) {
        return ofWeek(week, hour, minutes, 0);
    }

    public static DateTime ofWeek(int week, int hour, int minutes, int seconds) {
        return ofWeek(week, hour, minutes, seconds, 0);
    }

    public static DateTime ofWeek(int week, int hour, int minutes, int seconds, int millis) {
        DateTime time = DateTime.now();
        if (time.getDayOfWeek() < week) {
            time = time.withDayOfWeek(week);
        } else if (time.getDayOfWeek() > week) {
            time = time.withDayOfWeek(week).plusWeeks(1);
        }
        return time.withTime(hour, millis, seconds, millis);
    }

    public static int years(ReadableInstant from, ReadableInstant to) {
        return Years.yearsBetween(from, to).getYears();
    }

    public static int years(ReadablePartial from, ReadablePartial to) {
        return Years.yearsBetween(from, to).getYears();
    }

    public static int months(ReadableInstant from, ReadableInstant to) {
        return Months.monthsBetween(from, to).getMonths();
    }

    public static int months(ReadablePartial from, ReadablePartial to) {
        return Months.monthsBetween(from, to).getMonths();
    }

    public static int weeks(ReadableInstant from, ReadableInstant to) {
        return Weeks.weeksBetween(from, to).getWeeks();
    }

    public static int weeks(ReadablePartial from, ReadablePartial to) {
        return Weeks.weeksBetween(from, to).getWeeks();
    }

    public static int days(ReadableInstant from, ReadableInstant to) {
        return Days.daysBetween(from, to).getDays();
    }

    public static int days(ReadablePartial from, ReadablePartial to) {
        return Days.daysBetween(from, to).getDays();
    }

    public static int hours(ReadableInstant from, ReadableInstant to) {
        return Hours.hoursBetween(from, to).getHours();
    }

    public static int hours(ReadablePartial from, ReadablePartial to) {
        return Hours.hoursBetween(from, to).getHours();
    }

    public static int minutes(ReadableInstant from, ReadableInstant to) {
        return Minutes.minutesBetween(from, to).getMinutes();
    }

    public static int minutes(ReadablePartial from, ReadablePartial to) {
        return Minutes.minutesBetween(from, to).getMinutes();
    }

    public static int seconds(ReadableInstant from, ReadableInstant to) {
        return Seconds.secondsBetween(from, to).getSeconds();
    }

    public static int seconds(ReadablePartial from, ReadablePartial to) {
        return Seconds.secondsBetween(from, to).getSeconds();
    }

}
