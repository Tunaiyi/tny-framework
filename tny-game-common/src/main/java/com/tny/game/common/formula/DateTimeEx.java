package com.tny.game.common.formula;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

public class DateTimeEx {

    public static int days(DateTime start, DateTime end) {
        return Days.daysBetween(start, end).getDays();
    }

    public static int days(LocalDate start, LocalDate end) {
        return Days.daysBetween(start, end).getDays();
    }

}
