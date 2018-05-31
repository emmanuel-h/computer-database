package com.excilys.cdb.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ComputersToDeleteValidatorTest {

    /**
     * Test if it fails when giving a null reference.
     */
    @Test
    public void verifyListToDeleteWithNull() {
        assertFalse(ComputersToDeleteValidator.verifyListToDelete(null));
    }

    /**
     * Test if it fails when omitting the first parenthesis.
     */
    @Test
    public void verifyListToDeleteWithBadFirstChar() {
        assertFalse(ComputersToDeleteValidator.verifyListToDelete("1,2)"));
    }

    /**
     * Test if it fails when omitting the last parenthesis.
     */
    @Test
    public void verifyListToDeleteWithBadLastChar() {
        assertFalse(ComputersToDeleteValidator.verifyListToDelete("(1,2"));
    }

    /**
     * Test if it fails when giving no numbers.
     */
    @Test
    public void verifyListToDeleteWithEmptyBody() {
        assertFalse(ComputersToDeleteValidator.verifyListToDelete("()"));
    }

    /**
     * Test if it fails when giving a double number.
     */
    @Test
    public void verifyListToDeleteWithDouble() {
        assertFalse(ComputersToDeleteValidator.verifyListToDelete("(1.O,2)"));
    }

    /**
     * Test if it fails when giving an alphabetic character.
     */
    @Test
    public void verifyListToDeleteWithAlphabetic() {
        assertFalse(ComputersToDeleteValidator.verifyListToDelete("(a,2)"));
    }

    /**
     * Test if it works when the string is well-made.
     */
    @Test
    public void verifyListToDelete() {
        assertTrue(ComputersToDeleteValidator.verifyListToDelete("(1,2)"));
    }

}
