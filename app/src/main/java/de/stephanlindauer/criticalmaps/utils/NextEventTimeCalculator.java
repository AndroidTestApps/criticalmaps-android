package de.stephanlindauer.criticalmaps.utils;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class NextEventTimeCalculator {
    public static ArrayList<Date> getNextThreeCriticalMassDates() {
        Calendar calendar = new GregorianCalendar().getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        ArrayList<Date> dateList = new ArrayList<Date>();

        while (dateList.size() < 3) {
            if (isLastFridayOfTheMonth(calendar)) {
                Date asDate = new Date(calendar.getTimeInMillis());
                if (asDate.getTime() > new Date().getTime()) {
                    dateList.add(asDate);
                }
            }

            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return dateList;
    }

    private static boolean isLastFridayOfTheMonth(Calendar calendar) {
        GregorianCalendar calendarOneWeekAfter;
        calendarOneWeekAfter = (GregorianCalendar) calendar.clone();
        calendarOneWeekAfter.add(Calendar.WEEK_OF_YEAR, 1);

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && calendar.get(Calendar.MONTH) != calendarOneWeekAfter.get(Calendar.MONTH)) {
            return true;
        }
        return false;
    }


}
