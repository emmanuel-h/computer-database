package com.excilys.cdb.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

/**
 * The class describing a computer.
 * @author emmanuelh
 */
public class CompanyTest {

    Company company;

    /**
     * Create a test computer for each test.
     */
    @Before
    public void setup() {
        company = new Company(1L, "companyTest", 0, "https://upload.wikimedia.org/wikipedia/commons/a/ac/No_image_available.svg");
    }

    /**
     * Test if the computer's hashcode is correct.
     */
    @Test
    public void computerHashCode() {
        assertEquals(company.hashCode(), -508398961);
    }

    /**
     * Test if the equals method with a null reference works.
     */
    @Test
    public void computerEqualsWithNull() {
        assertFalse(company.equals(null));
    }

    /**
     * Test if the equals method with a different object works.
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void computerEqualsWithAnotherObject() {
        assertFalse(company.equals(new Computer()));
    }

    /**
     * Test if the equals method with a Computer having a different id works.
     */
    @Test
    public void computerEqualsWithAnotherId() {
        Company anotherCompany = new Company(2L, "companyTest", 0, "https://upload.wikimedia.org/wikipedia/commons/a/ac/No_image_available.svg");
        assertFalse(company.equals(anotherCompany));
    }

}
