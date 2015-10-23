package com.vmware.vchs.load.generator.util;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.Date;
/**
 * Created by liuda on 6/19/15.
 */
public class TimeUtil {
    public static LocalDateTime fromTimeStamp(String timeStamp){
        long time=-1;
        if(timeStamp.length()==10){
            time=Long.valueOf(timeStamp) * 1000;
        }
        else if(timeStamp.length()==13){
            time=Long.valueOf(timeStamp);
        }
        return new DateTime(time).withZone(DateTimeZone.UTC).toLocalDateTime();
    }

    public static String fromDate(LocalDateTime date){
        return "logstash-"+date.getYear()+"."+date.getMonthOfYear()+"."+date.getDayOfMonth();
    }

    public static String formatInfluxDbTime(LocalDateTime date){
        return date.toString().replace("T"," ");
    }
}
