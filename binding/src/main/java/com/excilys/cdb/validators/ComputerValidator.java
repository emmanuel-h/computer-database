package com.excilys.cdb.validators;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

public class ComputerValidator {

    /**
     * Verify the computer's name is not blank.
     * @param name  The name to test
     * @return      true if the name is valid, false if not
     */
    public static boolean validName(String name) {
        return !StringUtils.isBlank(name);
    }

    /**
     * Verify if the introduced date is before the discontinued date.
     * @param introduced    The introduced date
     * @param discontinued  The discontinued date
     * @return              true if the date are correct, false if not
     */
    public static boolean discontinuedGreaterThanIntroduced(LocalDate introduced, LocalDate discontinued) {
        return discontinued.isAfter(introduced);
    }

}
