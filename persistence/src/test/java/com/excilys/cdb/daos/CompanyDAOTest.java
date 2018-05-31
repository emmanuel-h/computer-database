package com.excilys.cdb.daos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.excilys.cdb.Page;
import com.excilys.cdb.configs.SpringConfigTest;
import com.excilys.cdb.model.Company;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigTest.class)
public class CompanyDAOTest {

    @Autowired
    private CompanyDAO companyDAO;

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(CompanyDAOTest.class);


    /**
     * Test if the findAll method works.
     */
    @Test
    public void findAll() {
        List<Company> companies = companyDAO.findAll();
        assertTrue(companies.size() == 42);
    }

    /**
     * Test if findAll with negative page number returns null.
     */
    @Test
    public void findAllWithNegativePage() {
        Page<Company> companies = companyDAO.findAllWithPaging(-1, 10);
        assertNull(companies);
    }

    /**
     * Test if findAll with negative number of results per page returns null.
     */
    @Test
    public void findAllWithNegativeResultsPerPage() {
        Page<Company> companies = companyDAO.findAllWithPaging(1, -1);
        assertNull(companies);
    }

    /**
     * Test if findAll with a too big page number return an empty page.
     */
    @Test
    public void findAllWithTooBigPage() {
        Page<Company> companies = companyDAO.findAllWithPaging(9999999, 10);
        assertTrue(companies.getResults().size() == 0);
    }

    /**
     * Test if findAll with a good page number works.
     */
    @Test
    public void findAllWithGoodPageNumber() {
        Page<Company> companies = companyDAO.findAllWithPaging(1, 5);
        assertTrue(companies.getResults().size() == 5);
    }

    /**
     * Test if the method findById works with a valid id.
     */
    @Test
    public void findByIdWithGoodId() {
        Optional<Company> company = companyDAO.findById(1L);
        assertTrue(company.get().getId() == 1L);
        assertTrue(company.get().getName().equals("Apple Inc."));
    }

    /**
     * Test if the method findById works with a bad id.
     */
    @Test
    public void findByIdWithBadId() {
        Optional<Company> company = companyDAO.findById(0L);
        assertFalse(company.isPresent());
    }

    /**
     * Test if the add method work.
     */
    @Test
    public void add() {
        Company company = new Company(500L, "test add");
        long addResult = companyDAO.add(company);
        assertTrue(addResult == 44L);
        companyDAO.delete(44L);
    }

    /**
     * Test if the add method with an existing id work.
     */
    @Test
    public void addWithexistingId() {
        Company company = new Company(1L, "test with exisiting id");
        long addResult = companyDAO.add(company);
        LOGGER.error("DZQDZD : " + addResult);
        assertTrue(addResult == 45L);
        companyDAO.delete(45L);
    }

    /**
     * Test if the deletion of a company works.
     */
    @Test
    public void deleteLegitCompany() {
        boolean deleteResult = companyDAO.delete(43L);
        assertTrue(deleteResult);
        Optional<Company> company = companyDAO.findById(43L);
        assertFalse(company.isPresent());
    }

    /**
     * Test if the deletion of a company works.
     */
    @Test
    public void deleteUnknownCompany() {
        boolean deleteResult = companyDAO.delete(50L);
        assertFalse(deleteResult);
    }

    /**
     * Test if the deletion of a company with a negative id works.
     */
    @Test
    public void deleteCompanyWithNegativeId() {
        boolean deleteResult = companyDAO.delete(-1L);
        assertFalse(deleteResult);
    }

    /**
     * Test if a complete update of a company works.
     */
    @Test
    public void updateWithAllFieldsFilled() {
        Company companyToUpdate = new Company(5, "test update");
        Company companyUpdated = companyDAO.update(companyToUpdate);
        assertTrue(companyUpdated.getName().equals("test update"));
    }

    /**
     * Test if an update with name null for a company works.
     */
    @Test
    public void updateWithNullFields() {
        Company companyToUpdate = new Company(5, null);
        Company companyUpdated = companyDAO.update(companyToUpdate);
        assertNull(companyUpdated.getName());
    }

    /**
     * Test if an update with an unknown id returns null.
     */
    @Test
    public void updateWithBadId() {
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
