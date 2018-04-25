package com.excilys.cdb.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.services.GeneralServiceTest;

public class DateConvertorTest {

    private final String DATE_CONVERTOR_EXCEPTION = "Exception when running test : ";
    private final Logger LOGGER = LoggerFactory.getLogger(GeneralServiceTest.class);

    /**
     * Test if the timeStampToLocalDate method works.
     */
    @Test
    public void timeStampToLocalDate() {
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date;
            date = dateFormat.parse("11/11/2007");
            Timestamp timestamp = new Timestamp(date.getTime());

            LocalDate localDate = LocalDate.of(2007, 11, 11);

            LocalDate localDateToTest = DateConvertor.timeStampToLocalDate(timestamp);

            assertEquals(localDate, localDateToTest);
        } catch (ParseException e) {
            LOGGER.warn(DATE_CONVERTOR_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the timeStampToLocalDate method works when passing a null Timestamp.
     */
    @Test
    public void timeStampToLocalDateWithNull() {
        LocalDate localDateToTest = DateConvertor.timeStampToLocalDate(null);
        assertNull(localDateToTest);
    }

    /**
     * Test if the localDateToTimestamp method works.
     */
    @Test
    public void localDateToTimeStamp() {
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date;
            date = dateFormat.parse("11/11/2007");
            Timestamp timestamp = new Timestamp(date.getTime());

            LocalDate localDate = LocalDate.of(2007, 11, 11);

            Timestamp timestampToTest = DateConvertor.localDateToTimeStamp(localDate);

            assertEquals(timestamp, timestampToTest);
        } catch (ParseException e) {
            LOGGER.warn(DATE_CONVERTOR_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the localDateToTimestamp method works when passing a null parameter.
     */
    @Test
    public void localDateToTimeStampWithNull() {
        Timestamp timestamp = DateConvertor.localDateToTimeStamp(null);
        assertNull(timestamp);
    }
}
