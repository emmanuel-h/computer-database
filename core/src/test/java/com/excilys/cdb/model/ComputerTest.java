package com.excilys.cdb.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

/**
 * The class describing a computer.
 * @author emmanuelh
 */
public class ComputerTest {

    Computer computer;

    /**
     * Create a test computer for each test.
     */
    @Before
    public void setup() {
        computer = new Computer.Builder("test builder")
                .id(30L)
                .introduced(LocalDate.of(2007, 11, 11))
                .discontinued(LocalDate.of(2008, 11, 11))
                .manufacturer(new Company(1L))
                .build();
    }

    /**
     * Test if the builder create a valid instance of Computer.
     */
    @Test
    public void builderWithName() {
        assertEquals(computer.getId(), 30L);
        assertTrue(computer.getName().equals("test builder"));
        assertTrue(computer.getIntroduced().equals(LocalDate.of(2007, 11, 11)));
        assertTrue(computer.getDiscontinued().equals(LocalDate.of(2008, 11, 11)));
        assertEquals(computer.getManufacturer(), new Company(1L));
    }

    /**
     * Test if the computer's String is well made.
     */
    @Test
    public void computerToString() {
        assertTrue(computer.toString().equals("Computer [id=" + computer.getId() + ", name=" + computer.getName()
        + ", introduced=" + computer.getIntroduced() + ", discontinued=" + computer.getDiscontinued()
        + ", manufacturer=" + computer.getManufacturer() + "]"));
    }

    /**
     * Test if the computer's hashcode is correct.
     */
    @Test
    public void computerHashCode() {
        assertEquals(computer.hashCode(), -1076327914);
    }

    /**
     * Test if the equals method with a null reference works.
     */
    @Test
    public void computerEqualsWithNull() {
        assertFalse(computer.equals(null));
    }

    /**
     * Test if the equals method with a different object works.
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void computerEqualsWithAnotherObject() {
        assertFalse(computer.equals(new Company()));
    }

    /**
     * Test if the equals method with a Computer having a different id works.
     */
    @Test
    public void computerEqualsWithAnotherId() {
        Computer anotherComputer = new Computer.Builder("another id").id(1L).build();
        assertFalse(computer.equals(anotherComputer));
    }

}
