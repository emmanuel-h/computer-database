package com.excilys.cdb.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.excilys.cdb.daos.DAOFactory.DaoTypes;
import com.excilys.cdb.exceptions.FactoryException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.utils.Page;

public class ComputerDAOTest {

    private ComputerDAO computerDAO;
    private CompanyDAO companyDAO;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Get the instance of computerDAO.
     * @throws FactoryException If there is an exception in the DAOFactory
     */
    @Before
    public void setup() throws FactoryException {
        computerDAO = (ComputerDAO) DAOFactory.getDAO(DaoTypes.COMPUTER);
        companyDAO = (CompanyDAO) DAOFactory.getDAO(DaoTypes.COMPANY);
    }

    /**
     * Test if findAll with a negative page number return null.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findAllWithNegativePage() throws SQLException {
        Page<Computer> computers = computerDAO.findAllWithPaging(-1, 10);
        assertNull(computers);
    }

    /**
     * Test if findAll with a negative number of results per page return null.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findAllWithNegativeResultsPerPage() throws SQLException {
        Page<Computer> computers = computerDAO.findAllWithPaging(1, -1);
        assertNull(computers);
    }

    /**
     * Test if findAll with a too big page number return an empty page.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findAllWithTooBigPage() throws SQLException {
        Page<Computer> computers = computerDAO.findAllWithPaging(9999999, 10);
        assertTrue(computers.getResults().size() == 0);
    }

    /**
     * Test if findAll with a good page number works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findAllWithGoodPageNumber() throws SQLException {
        Page<Computer> computers = computerDAO.findAllWithPaging(1, 5);
        assertTrue(computers.getResults().size() == 5);
    }

    /**
     * Test if the method findById works with a valid id.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findByIdWithGoodId() throws SQLException {
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
     * @throws SQLException If there is a problem with the database
     */
    public void findByIdWithManufacturer() throws SQLException {
        Optional<Computer> computer = computerDAO.findById(7L);
        assertNull(computer.get().getManufacturer());
    }

    /**
     * Test if the method findById works with the creation of a manufacturer.
     * @throws SQLException If there is a problem with the database
     */
    public void findByIdWithNoManufacturer() throws SQLException {
        Optional<Computer> computer = computerDAO.findById(1L);
        Company company = computer.get().getManufacturer();
        assertTrue(company.getId() == 1L);
    }

    /**
     * Test if the method findById works with a bad id.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findByIdWithBadId() throws SQLException {
        Optional<Computer> computer = computerDAO.findById(0L);
        assertFalse(computer.isPresent());
    }

    /**
     * Test if the add method with all computer's field work.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void addWithAllFields() throws SQLException {
        Optional<Company> manufacturer = companyDAO.findById(1L);
        Computer computer = new Computer.Builder("test")
                .introduced(LocalDate.of(2007, 12, 12))
                .discontinued(LocalDate.of(2008, 12, 12))
                .manufacturer(manufacturer.get())
                .build();
        long addResult = computerDAO.add(computer);
        assertTrue(addResult == 13L);
        computerDAO.delete(13L);
    }

    /**
     * Test if the add method with no computer's field work.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void addWithNoFields() throws SQLException {
        Computer computer = new Computer.Builder("test").build();
        long addResult = computerDAO.add(computer);
        assertTrue(addResult == 14L);
        computerDAO.delete(14L);
    }

    /**
     * Test if the add method with an existing id work.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void addWithexistingId() throws SQLException {
        Computer computer = new Computer.Builder("test").id(1L).build();
        long addResult = computerDAO.add(computer);
        assertTrue(addResult == 1L);
    }

    /**
     * Test if the deletion of a computer works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void deleteLegitComputer() throws SQLException {
        boolean deleteResult = computerDAO.delete(1L);
        assertTrue(deleteResult);
        Optional<Computer> computer = computerDAO.findById(1L);
        assertFalse(computer.isPresent());
    }

    /**
     * Test if the deletion of a computer works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void deleteUnknownComputer() throws SQLException {
        boolean deleteResult = computerDAO.delete(50L);
        assertFalse(deleteResult);
    }

    /**
     * Test if the deletion of a computer with a negative id works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void deleteComputerWithNegativeId() throws SQLException {
        boolean deleteResult = computerDAO.delete(-1L);
        assertFalse(deleteResult);
    }

    /**
     * Test if a complete update of a computer works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void updateWithAllFieldsFilled() throws SQLException {
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
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void updateWithNullFields() throws SQLException {
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
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void updateWithBadId() throws SQLException {
        Computer computerToUpdate = new Computer.Builder("bad id")
                .id(40L)
                .build();
        Computer computerUpdated = computerDAO.update(computerToUpdate);
        assertNull(computerUpdated);
    }

    /**
     * Test if a update with an unknown company id raise an exception.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void updateWithBadCompanyId() throws SQLException {
        Computer computerToUpdate = new Computer.Builder("bad company id")
                .id(2L)
                .manufacturer(new Company(500L))
                .build();
        exception.expect(SQLException.class);
        computerDAO.update(computerToUpdate);
    }

    /**
     * Test if the count retrieve the number of computers in the database.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void count() throws SQLException {
        int resultCount = computerDAO.count();
        assertEquals(11, resultCount);
    }

    /**
     * Test if the multiple delete works fine.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void deleteMultiple() throws SQLException {
        Optional<Computer> computer = computerDAO.findById(5L);
        boolean test = computerDAO.deleteMultiple("(5)");
        assertTrue(test);
        computerDAO.add(computer.get());
    }

    /**
     * Test if the search of a computer works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void searchComputer() throws SQLException {
        Page<Computer> computers = computerDAO.searchComputer("pp", 1, 10);
        assertTrue(computers.getCurrentPage() == 1);
        assertTrue(computers.getMaxPage() == 1);
        assertTrue(computers.getResults().size() == 6);
        assertTrue(computers.getResultsPerPage() == 10);
    }

    /**
     * Destroy the computerDAO at the end of the test.
     */
    @After
    public void tearDown() {
        computerDAO = null;
    }
}
