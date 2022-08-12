/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.utils;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeAide {

    private DateTimeAide() {
    }

    /**
     * HH:mm
     */
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    /**
     * yyyy-MM-dd
     */
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    /**
     * yyyy-MM-dd HH:mm
     */
    public static final DateTimeFormatter DATE_TIME_MIN_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    /**
     * yyyyMMddHHmmss
     */
    public static final DateTimeFormatter DATE_TIME_2_NUM_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.systemDefault());

    /**
     * yyyyMMddHHmmss
     */
    public static final DateTimeFormatter DATE_T_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    /**
     * MM/dd/yyyy
     */
    public static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault());

    public static ZonedDateTime date(String date) throws ParseException {
        return ZonedDateTime.parse(date, DATE_FORMAT);
    }

    public static ZonedDateTime date(String date, DateTimeFormatter formatter) throws ParseException {
        return ZonedDateTime.parse(date, DATE_FORMAT);
    }

    public static ZonedDateTime dateTime(String date) throws ParseException {
        return ZonedDateTime.parse(date, DATE_TIME_FORMAT);
    }

    public static ZonedDateTime dateTime(String date, DateTimeFormatter formatter) throws ParseException {
        return ZonedDateTime.parse(date, formatter);
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now();
    }

    public static LocalDate today() {
        DateRange range = NOW_RANGE;
        if (range.isSameDate()) {
            return range.date;
        }
        NOW_RANGE = range = new DateRange();
        return range.date;
    }

    public static LocalDate int2Date(int dateInt) {
        return LocalDate.of(dateInt / 10000, (dateInt / 100) % 100, dateInt % 100);
    }

    public static int date2Int(LocalDate now) {
        return (((now.getYear() * 100) + now.getMonthValue()) * 100) + now.getDayOfMonth();
    }

    public static int date2Int(Instant now) {
        return date2Int(LocalDate.from(now));
    }

    public static int date2Int(LocalDateTime now) {
        return (((now.getYear() * 100) + now.getMonthValue()) * 100) + now.getDayOfMonth();
    }

    public static long time2DateLong(LocalDateTime now) {
        return ((((100L * now.getYear()) +
                now.getMonthValue()) * 100) +
                now.getDayOfMonth());
    }

    public static long time2MillisLong(LocalDateTime now) {
        return ((((((((((100L * now.getYear()) +
                now.getMonthValue()) * 100) +
                now.getDayOfMonth()) * 100) +
                now.getHour()) * 100) +
                now.getMinute()) * 100) +
                now.getSecond()) * 1000 +
                now.getNano() / 1000;
    }

    public static LocalDateTime millisLong2Time(long timeInt) {
        int millis = (int)(timeInt % 1000);
        int seconds = (int)((timeInt = timeInt / 1000) % 100);
        int minute = (int)((timeInt = timeInt / 100) % 100);
        int hour = (int)((timeInt = timeInt / 100) % 100);
        int day = (int)((timeInt = timeInt / 100) % 100);
        int month = (int)((timeInt = timeInt / 100) % 100);
        int year = (int)(timeInt / 100);
        return LocalDateTime.of(year, month, day, hour, minute, seconds, millis);
    }

    public static long time2Second(LocalDateTime now) {
        return (((((((((100L * now.getYear()) +
                now.getMonthValue()) * 100) +
                now.getDayOfMonth()) * 100) +
                now.getHour()) * 100) +
                now.getMinute()) * 100) +
                now.getSecond();
    }

    public static long time2Minute(LocalDateTime now) {
        return ((((((((100L * now.getYear()) +
                now.getMonthValue()) * 100) +
                now.getDayOfMonth()) * 100) +
                now.getHour()) * 100) +
                now.getMinute());
    }

    public static LocalDateTime second2Time(long timeInt) {
        int seconds = (int)(timeInt % 100);
        int minute = (int)((timeInt = timeInt / 100) % 100);
        int hour = (int)((timeInt = timeInt / 100) % 100);
        int day = (int)((timeInt = timeInt / 100) % 100);
        int month = (int)((timeInt = timeInt / 100) % 100);
        int year = (int)(timeInt / 100);
        return LocalDateTime.of(year, month, day, hour, minute, seconds);
    }

    private static class DateRange {

        LocalDate date;

        long start;

        long end;

        public DateRange() {
            this.date = LocalDate.now();
            ZonedDateTime dateTime = ZonedDateTime
                    .of(this.date.getYear(), this.date.getMonthValue(), this.date.getDayOfMonth(), 0, 0, 0, 0, ZoneId.systemDefault());
            this.start = dateTime.toInstant().toEpochMilli();
            this.end = dateTime.plusDays(1).toInstant().toEpochMilli();
        }

        public boolean isSameDate() {
            long time = System.currentTimeMillis();
            return this.start <= time && time < this.end;
        }

    }

    private static volatile DateRange NOW_RANGE = new DateRange();

}
