package unitrack.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Step 21: Date Formatting & Arithmetic Utilities
 * Provides clean helpers for calendar dates, times, and day-of-week abbreviations.
 */
public class DateUtils {
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE_SHORT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Formats date with day name and ordinal suffix (e.g. "Friday, October 24th")
     * Matches the left panel header of the reference calendar image!
     */
    public static String formatWithDayAndOrdinal(LocalDate date) {
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int dayOfMonth = date.getDayOfMonth();
        return String.format("%s, %s %d%s", dayOfWeek, month, dayOfMonth, getOrdinalSuffix(dayOfMonth));
    }

    /**
     * Computes the English ordinal suffix (st, nd, rd, th)
     */
    public static String getOrdinalSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

    /**
     * Formats LocalTime to "HH:mm" (e.g. "08:30")
     */
    public static String formatTime(LocalTime time) {
        if (time == null) return "--:--";
        return time.format(TIME_FORMATTER);
    }

    /**
     * Returns 3-letter month abbreviation (e.g. "OCT")
     */
    public static String getShortMonthName(int monthValue) {
        return java.time.Month.of(monthValue).getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
    }
}
