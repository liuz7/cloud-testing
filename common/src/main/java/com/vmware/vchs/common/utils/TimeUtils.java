package com.vmware.vchs.common.utils;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * The time utils for restore time check.
 */
public class TimeUtils {

    /**
     * @return Get current wall time and format to String.
     */
    public static String getDateTime() {
        DateTime dateTime = new DateTime();
        return getDateTime(dateTime);
    }

    /**
     * @param dateTime a joda dateTime object
     * @return format the given dateTime to ISO 8601 compatible String.
     */
    public static String getDateTime(DateTime dateTime) {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeNoMillis();
        return fmt.withZoneUTC().print(dateTime);
    }

    /**
     * @param date an ISO 8601 format date time
     * @return an joda date time object
     */
    public static DateTime parseDate(String date) {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeNoMillis();
        return fmt.parseDateTime(date);
    }

    public static boolean isBetween(String from, String to, String dateTime) {
        return !parseDate(dateTime).isBefore(parseDate(from)) && !parseDate(dateTime).isAfter(parseDate(to));
    }

    public static boolean isBetween(String[] range, String dateTime) {
        return !parseDate(dateTime).isBefore(parseDate(range[0])) && !parseDate(dateTime).isAfter(parseDate(range[1]));
    }

    public static Duration getDuration(String from, String to) {
        return new Duration(parseDate(from), parseDate(to));
    }

    public static Duration getDuration(String[] range) {
        return new Duration(parseDate(range[0]), parseDate(range[1]));
    }

    public static long getDurationInSeconds(String[] range) {
        return getDuration(range).getStandardSeconds();
    }

    public static String getMiddleDataTime(String[] range) {
        long halfDuration = getDurationInSeconds(range) / 2;
        return getDateTime(parseDate(range[1]).minusSeconds((int) halfDuration));
    }

    public static DateTime toISODateTime(String datetime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSS").withZoneUTC();
        return dateTimeFormatter.parseDateTime(datetime);
    }

    public static void main(String[] args) {
        String[] range = new String[]{"2015-04-01T09:31:06Z", "2015-04-01T09:31:06Z"};
        System.out.println(getMiddleDataTime(range));
    }

}
