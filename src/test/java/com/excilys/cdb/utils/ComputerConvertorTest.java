package com.excilys.cdb.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

import com.excilys.cdb.dtos.ComputerDTO;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

public class ComputerConvertorTest {

    /**
     * Test if the Computer to ComputerDTO works with null fields.
     */
    @Test
    public void computerToDTOWithNullFields() {
        Computer computer = new Computer.Builder("test").build();
        ComputerDTO computerDTO = ComputerConvertor.computerToDTO(computer);
        assertTrue(computerDTO.getId() == 0L);
        assertTrue(computerDTO.getName().equals("test"));
        assertNull(computerDTO.getIntroduced());
        assertNull(computerDTO.getDiscontinued());
        assertNull(computerDTO.getManufacturer());
    }

    /**
     * Test if the Computer to ComputerDTO works with all fields fill.
     */
    @Test
    public void computerToDTOWithAllFields() {
        Company company = new Company(1L, "company test");
        Computer computer = new Computer.Builder("test")
                .id(1L)
                .introduced(LocalDate.of(2007, 11, 11))
                .discontinued(LocalDate.of(2008, 11, 11))
                .manufacturer(company)
                .build();
        ComputerDTO computerDTO = ComputerConvertor.computerToDTO(computer);
        assertTrue(computerDTO.getId() == 1L);
        assertTrue(computerDTO.getName().equals("test"));
        assertEquals(LocalDate.of(2007, 11, 11).toString(), computerDTO.getIntroduced());
        assertEquals(LocalDate.of(2008, 11, 11).toString(), computerDTO.getDiscontinued());
        assertEquals(company.getName(), computerDTO.getManufacturer());
    }

    /**
     * Test if the ComputerDTO to Computer works with null fields.
     */
    @Test
    public void dTOToComputerWithNullFields() {
        ComputerDTO computerDTO = new ComputerDTO();
        computerDTO.setName("test computer dto");
        Computer computer = ComputerConvertor.dTOToComputer(computerDTO);
        assertTrue(computer.getId() == 0L);
        assertTrue(computer.getName().equals("test computer dto"));
        assertNull(computer.getIntroduced());
        assertNull(computer.getDiscontinued());
        assertNull(computer.getManufacturer());
    }

    /**
     * Test if the ComputerDTo to COmputer works with all fields fill.
     */
    @Test
    public void dTOToComputerWithAllFields() {
        Company company = new Company(1L, "company test");

        ComputerDTO computerDTO = new ComputerDTO();
        computerDTO.setName("test computer dto");
        computerDTO.setId(1L);
        computerDTO.setIntroduced(LocalDate.of(2007, 11, 11).toString());
        computerDTO.setDiscontinued(LocalDate.of(2008, 11, 11).toString());
        computerDTO.setManufacturer("company test");

        Computer computer = ComputerConvertor.dTOToComputer(computerDTO);
        assertTrue(computer.getId() == 1L);
        assertTrue(computer.getName().equals("test computer dto"));
        assertEquals(LocalDate.of(2007, 11, 11), computer.getIntroduced());
        assertEquals(LocalDate.of(2008, 11, 11), computer.getDiscontinued());
        assertEquals(company.getName(), computer.getManufacturer().getName());
    }

}
