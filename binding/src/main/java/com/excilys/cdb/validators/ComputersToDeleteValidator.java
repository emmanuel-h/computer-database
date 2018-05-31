package com.excilys.cdb.validators;

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public class ComputersToDeleteValidator {

    /**
     * Verify if the list of computer's identifiers to delete is correct.
     * @param toDelete  The deletion list to verify
     * @return          True if the list is correct, false if not
     */
    public static boolean verifyListToDelete(String toDelete) {
        if (null == toDelete) {
            return false;
        }
        if ('(' != toDelete.charAt(0)) {
            return false;
        }
        if (')' != toDelete.charAt(toDelete.length() - 1)) {
            return false;
        }
        StringBuilder idsToDeleteSB = new StringBuilder(toDelete);
        idsToDeleteSB.deleteCharAt(0);
        idsToDeleteSB.deleteCharAt(idsToDeleteSB.length() - 1);
        if (StringUtils.isEmpty(idsToDeleteSB.toString())) {
            return false;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(idsToDeleteSB.toString(), ",");
        while (stringTokenizer.hasMoreTokens()) {
            String stringToTest = (String) stringTokenizer.nextElement();
            if (!StringUtils.isNumeric(stringToTest)) {
                return false;
            }
        }
        return true;
    }

}
