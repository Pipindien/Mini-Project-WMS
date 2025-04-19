package com.portfolio_summary_service.app.utility;


import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateHelper {
    public static int calculateMonthDiff(Date from, LocalDate to) {
        // Convert Date (java.util.Date) ke LocalDate
        LocalDate fromDate = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Period period = Period.between(fromDate, to);
        return period.getYears() * 12 + period.getMonths();
    }

    public static long calculateDayDiff(Date from, LocalDate to) {
        LocalDate fromDate = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(fromDate, to);
    }

}

