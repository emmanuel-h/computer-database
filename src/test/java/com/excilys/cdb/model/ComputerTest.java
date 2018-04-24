package com.excilys.cdb.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

/**
 * The class describing a computer.
 * @author emmanuelh
 */
public class ComputerTest {

    /**
     * Test if the builder create a valid instance of Computer.
     */
    @Test
    public void builderWithName() {
        Computer computer = new Computer.Builder("test builder")
                .id(30L)
                .introduced(LocalDate.of(2007, 11, 11))
                .discontinued(LocalDate.of(2008, 11, 11))
                .manufacturer(new Company(1L))
                .build();
        assertEquals(computer.getId(), 30L);
        assertTrue(computer.getName().equals("test builder"));
        assertTrue(computer.getIntroduced().equals(LocalDate.of(2007, 11, 11)));
        assertTrue(computer.getDiscontinued().equals(LocalDate.of(2008, 11, 11)));
        assertEquals(computer.getManufacturer(), new Company(1L));
    }

}
