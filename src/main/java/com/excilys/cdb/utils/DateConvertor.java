package main.java.com.excilys.cdb.utils;

import java.sql.Timestamp;
import java.time.LocalDate;

public class DateConvertor {
	    
    public static LocalDate TimeStampToLocalDate(final Timestamp time) {
        if(null != time) {
            return time.toLocalDateTime().toLocalDate();
        }
        return null;
        
    }
    
    public static Timestamp LocalDateToTimeStamp(final LocalDate date) {
        if(null != date) {
            return Timestamp.valueOf(date.atStartOfDay());
        }
        return null;
        
    }
}
