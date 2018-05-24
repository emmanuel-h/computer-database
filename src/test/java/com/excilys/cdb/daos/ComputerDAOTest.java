package com.excilys.cdb.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.excilys.cdb.configs.SpringConfigTest;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.utils.Page;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigTest.class)
public class ComputerDAOTest {

    @Autowired
    private ComputerDAO computerDAO;

    @Autowired
    private CompanyDAO companyDAO;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Test if findAll with a negative page number return null.
     */
    @Test
    public void findAllWithNegativePage() {
        Page<Computer> computers = computerDAO.findAllWithPaging(-1, 10);
        assertNull(computers);
    }

    /**
     * Test if findAll with a negative number of results per page return null.
     */
    @Test
    public void findAllWithNegativeResultsPerPage() {
        Page<Computer> computers = computerDAO.findAllWithPaging(1, -1);
        assertNull(computers);
    }

    /**
     * Test if findAll with a too big page number return an empty page.
     */
    @Test
    public void findAllWithTooBigPage() {
        Page<Computer> computers = computerDAO.findAllWithPaging(9999999, 10);
        assertTrue(computers.getResults().size() == 0);
    }

    /**
     * Test if findAll with a good page number works.
     */
    @Test
    public void findAllWithGoodPageNumber() {
        Page<Computer> computers = computerDAO.findAllWithPaging(1, 5);
        assertTrue(computers.getResults().size() == 5);
    }

    /**
     * Test if the method findById works with a valid id.
     */
    @Test
    public void findByIdWithGoodId() {
        Optional<Computer> computerOptional = computerDAO.findById(1L);
        assertTrue(computerOptional.isPresent());
        Computer computer = computerOptional.get();
        assertEquals(computer.getId(), 1L);
        assertEquals(computer.getName(), "MacBook Pro 15.4 inch");
        assertNull(computer.getIntroduced());
        assertNull(computer.getDiscontinued());
        assertTrue(computer.getManufacturer().getId() == 1);
    }

    /**
     * Test if the method findById works with no manufacturer.
     */
    public void findByIdWithManufacturer() {
        Optional<Computer> computer = computerDAO.findById(7L);
        assertNull(computer.get().getManufacturer());
    }

    /**
     * Test if the method findById works with the creation of a manufacturer.
     */
    public void findByIdWithNoManufacturer() {
        Optional<Computer> computer = computerDAO.findById(1L);
        Company company = computer.get().getManufacturer();
        assertTrue(company.getId() == 1L);
    }

    /**
     * Test if the method findById works with a bad id.
     */
    @Test
    public void findByIdWithBadId() {
        Optional<Computer> computer = computerDAO.findById(0L);
        assertFalse(computer.isPresent());
    }

    /**
     * Test if the add method with all computer's field work.
     */
    @Test
    public void addWithAllFields() {
        Optional<Company> manufacturer = companyDAO.findById(1L);
        Computer computer = new Computer.Builder("testWithAllFields")
                .introduced(LocalDate.of(2007, 12, 12))
                .discontinued(LocalDate.of(2008, 12, 12))
                .manufacturer(manufacturer.get())
                .build();
        long addResult = computerDAO.add(computer);
        assertTrue(addResult == 14L);
        computerDAO.delete(14L);
    }

    /**
     * Test if the add method with no computer's field work.
     */
    @Test
    public void addWithNoFields() {
        Computer computer = new Computer.Builder("testAddWithNoFields").build();
        long addResult = computerDAO.add(computer);
        assertTrue(addResult == 15L);
        computerDAO.delete(15L);
    }

    /**
     * Test if the add method with an existing id work.
     */
    @Test
    public void addWithexistingId() {
        Computer computer = new Computer.Builder("testAddWithExistingId").id(1L).build();
        long addResult = computerDAO.add(computer);
        assertTrue(addResult == 16L);
    }

    /**
     * Test if the deletion of a computer works.
     */
    @Test
    public void deleteLegitComputer() {
        boolean deleteResult = computerDAO.delete(1L);
        assertTrue(deleteResult);
        Optional<Computer> computer = computerDAO.findById(1L);
        assertFalse(computer.isPresent());
    }

    /**
     * Test if the deletion of a computer works.
     */
    @Test
    public void deleteUnknownComputer() {
        boolean deleteResult = computerDAO.delete(50L);
        assertFalse(deleteResult);
    }

    /**
     * Test if the deletion of a computer with a negative id works.
     */
    @Test
    public void deleteComputerWithNegativeId() {
        boolean deleteResult = computerDAO.delete(-1L);
        assertFalse(deleteResult);
    }

    /**
     * Test if a complete update of a computer works.
     */
    @Test
    public void updateWithAllFieldsFilled() {
        Computer computerToUpdate = new Computer.Builder("test")
                .id(2L)
                .introduced(LocalDate.of(2007, 11, 11))
                .discontinued(LocalDate.of(2008, 11, 11))
                .manufacturer(new Company(3L))
                .build();
        Computer computerUpdated = computerDAO.update(computerToUpdate);
        assertTrue(computerUpdated.getName().equals("test"));
        assertTrue(computerUpdated.getIntroduced().equals(LocalDate.of(2007, 11, 11)));
        assertTrue(computerUpdated.getDiscontinued().equals(LocalDate.of(2008, 11, 11)));
        assertTrue(computerUpdated.getManufacturer().getId() == 3L);
    }

    /**
     * Test if an update with null dates and manufacturer of a computer works.
     */
    @Test
    public void updateWithNullFields() {
        Computer computerToUpdate = new Computer.Builder("test")
                .id(2L)
                .introduced(null)
                .discontinued(null)
                .manufacturer(null)
                .build();
        Computer computerUpdated = computerDAO.update(computerToUpdate);
        assertTrue(computerUpdated.getName().equals("test"));
        assertNull(computerUpdated.getIntroduced());
        assertNull(computerUpdated.getDiscontinued());
        assertNull(computerUpdated.getManufacturer());
    }

    /**
     * Test if an update with an unknown id returns null.
     */
    @Test
    public void updateWithBadId() {
        Computer computerToUpdate = new Computer.Builder("bad id")
                .id(40L)
                .build();
        Computer computerUpdated = computerDAO.update(computerToUpdate);
        assertNull(computerUpdated);
    }

    /**
     * Test if a update with an unknown company id raise an exception.
     */
    @Test
    public void updateWithBadCompanyId() {
        Computer computerToUpdate = new Computer.Builder("bad company id")
                .id(2L)
                .manufacturer(new Company(500L))
                .build();
        exception.expect(org.springframework.dao.DataIntegrityViolationException.class);
        computerDAO.update(computerToUpdate);
    }

    /**
     * Test if the count retrieve the number of computers in the database.
     */
    @Test
    public void count() {
        int resultCount = computerDAO.count();
        assertEquals(11, resultCount);
    }

    /**
     * Test if the multiple delete works fine.
     */
    @Test
    public void deleteMultiple() {
        Optional<Computer> computer = computerDAO.findById(5L);
        boolean test = computerDAO.deleteMultiple("(5)");
        assertTrue(test);
        computerDAO.add(computer.get());
    }

    /**
     * Test if the search of a computer works.
     */
    @Test
    public void searchComputer() {
        Page<Computer> computers = computerDAO.searchComputer("pp", 1, 10);
        assertTrue(computers.getCurrentPage() == 1);
        assertTrue(computers.getResultsPerPage() == 10);
        assertTrue(computers.getMaxPage() == 1);
        assertTrue(computers.getResults().size() == 6);
    }

    /**
     * Destroy the computerDAO at the end of the test.
     */
    @After
    public void tearDown() {
        computerDAO = null;
    }
}
