package com.excilys.cdb.utils;

import java.sql.Timestamp;
import java.time.LocalDate;

public class DateConvertor {

    /**
     * Convert a TimeStamp into a LocalDate.
     * @param time  The TimeStamp
     * @return      A LocalDate
     */
    public static LocalDate timeStampToLocalDate(final Timestamp time) {
        if (null != time) {
            return time.toLocalDateTime().toLocalDate();
        }
        return null;

    }

    /**
     * Convert a LocalDate into a TimeStamp.
     * @param date  The LocalDate
     * @return      The TimeStamp
     */
    public static Timestamp localDateToTimeStamp(final LocalDate date) {
        if (null != date) {
            return Timestamp.valueOf(date.atStartOfDay());
        }
        return null;

    }
}
