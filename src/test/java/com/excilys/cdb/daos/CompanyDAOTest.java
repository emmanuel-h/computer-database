package com.excilys.cdb.daos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.excilys.cdb.daos.DAOFactory.DaoTypes;
import com.excilys.cdb.exceptions.FactoryException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.utils.Page;

public class CompanyDAOTest {

    private CompanyDAO companyDAO;

    /**
     * Get the instance of companyDAO.
     * @throws FactoryException If there is an exception in the DAOFactory
     */
    @Before
    public void setup() throws FactoryException {
        companyDAO = (CompanyDAO) DAOFactory.getDAO(DaoTypes.COMPANY);
    }


    /**
     * Test if the findAll method works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findAll() throws SQLException {
        List<Company> companies = companyDAO.findAll();
        assertTrue(companies.size() == 42);
    }

    /**
     * Test if findAll with negative page number returns null.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findAllWithNegativePage() throws SQLException {
        Page<Company> companies = companyDAO.findAllWithPaging(-1, 10);
        assertNull(companies);
    }

    /**
     * Test if findAll with negative number of results per page returns null.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findAllWithNegativeResultsPerPage() throws SQLException {
        Page<Company> companies = companyDAO.findAllWithPaging(1, -1);
        assertNull(companies);
    }

    /**
     * Test if findAll with a too big page number return an empty page.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findAllWithTooBigPage() throws SQLException {
        Page<Company> companies = companyDAO.findAllWithPaging(9999999, 10);
        assertTrue(companies.getResults().size() == 0);
    }

    /**
     * Test if findAll with a good page number works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findAllWithGoodPageNumber() throws SQLException {
        Page<Company> companies = companyDAO.findAllWithPaging(1, 5);
        assertTrue(companies.getResults().size() == 5);
    }

    /**
     * Test if the method findById works with a valid id.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findByIdWithGoodId() throws SQLException {
        Optional<Company> company = companyDAO.findById(1L);
        assertTrue(company.get().getId() == 1L);
        assertTrue(company.get().getName().equals("Apple Inc."));
    }

    /**
     * Test if the method findById works with a bad id.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void findByIdWithBadId() throws SQLException {
        Optional<Company> company = companyDAO.findById(0L);
        assertFalse(company.isPresent());
    }

    /**
     * Test if the add method work.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void add() throws SQLException {
        Company company = new Company(500L, "test add");
        long addResult = companyDAO.add(company);
        assertTrue(addResult == 44L);
        companyDAO.delete(44L);
    }

    /**
     * Test if the add method with an existing id work.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void addWithexistingId() throws SQLException {
        Company company = new Company(1L, "test with exisiting id");
        long addResult = companyDAO.add(company);
        assertTrue(addResult == 45L);
        companyDAO.delete(45L);
    }

    /**
     * Test if the deletion of a company works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void deleteLegitCompany() throws SQLException {
        boolean deleteResult = companyDAO.delete(43L);
        assertTrue(deleteResult);
        Optional<Company> company = companyDAO.findById(43L);
        assertFalse(company.isPresent());
    }

    /**
     * Test if the deletion of a company works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void deleteUnknownCompany() throws SQLException {
        boolean deleteResult = companyDAO.delete(50L);
        assertFalse(deleteResult);
    }

    /**
     * Test if the deletion of a company with a negative id works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void deleteCompanyWithNegativeId() throws SQLException {
        boolean deleteResult = companyDAO.delete(-1L);
        assertFalse(deleteResult);
    }

    /**
     * Test if a complete update of a company works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void updateWithAllFieldsFilled() throws SQLException {
        Company companyToUpdate = new Company(5, "test update");
        Company companyUpdated = companyDAO.update(companyToUpdate);
        assertTrue(companyUpdated.getName().equals("test update"));
    }

    /**
     * Test if an update with name null for a company works.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void updateWithNullFields() throws SQLException {
        Company companyToUpdate = new Company(5, null);
        Company companyUpdated = companyDAO.update(companyToUpdate);
        assertNull(companyUpdated.getName());
    }

    /**
     * Test if an update with an unknown id returns null.
     * @throws SQLException If there is a problem with the database
     */
    @Test
    public void updateWithBadId() throws SQLException {
        Company companyToUpdate = new Company(60, "bad id");
        Company companyUpdated = companyDAO.update(companyToUpdate);
        assertNull(companyUpdated);
    }

    /**
     * Destroy the companyDAO at the end of the test.
     */
    @After
    public void tearDown() {
        companyDAO = null;
    }

}
