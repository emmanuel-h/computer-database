package com.excilys.cdb.daos;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.excilys.cdb.daos.DAOFactory.DaoTypes;
import com.excilys.cdb.exceptions.FactoryException;
import com.excilys.cdb.model.Company;

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
     * Test if the method findByIUd works correctly.
     * @throws SQLException If there is a problem with the database.
     */
    @Test
    public void testFindById() throws SQLException {
        Company company = companyDAO.findById(1L);
        assertTrue(company.getId() == 1L);
        assertTrue(company.getName().equals("Apple Inc."));

        company = companyDAO.findById(0L);
        assertNull(company);
    }

    /**
     * Destroy the companyDAO at the end of the test.
     */
    @After
    public void tearDown() {
        companyDAO = null;
    }

}
