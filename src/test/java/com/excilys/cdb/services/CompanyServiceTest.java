package com.excilys.cdb.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.daos.CompanyDAO;
import com.excilys.cdb.daos.ComputerDAO;
import com.excilys.cdb.exceptions.FactoryException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.utils.Page;

@RunWith(MockitoJUnitRunner.class)
public class CompanyServiceTest {

    @Mock
    private CompanyDAO companyDAO;

    @Mock
    private ComputerDAO computerDAO;

    @InjectMocks
    private CompanyService service;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final Logger LOGGER = LoggerFactory.getLogger(CompanyServiceTest.class);
    private final String SQL_EXCEPTION = "Unit test - SQL Exception : ";

    /**
     * Initialize the mocks.
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test if the singleton's instance is well-created.
     * @throws FactoryException If there is an exception during instantiating the connection
     */
    @Test
    public void getInstance() throws FactoryException {
        CompanyService serviceTest = CompanyService.getInstance();
        assertTrue(serviceTest instanceof CompanyService);
    }

    /**
     * Test if the getAllCompanies method work properly.
     */
    @Test
    public void getAllCompaniesWithValidPageNumber() {
        Company company = new Company(1, "test");
        Page<Company> companies = new Page<>();
        List<Company> companiesList = new ArrayList<>();
        companiesList.add(company);
        companies.setResults(companiesList);

        try {
            Mockito.when(companyDAO.findAllWithPaging(1, 10)).thenReturn(companies);

            Optional<Page<Company>> resultsPage = service.getAllCompaniesWithPaging(1, 10);
            assertTrue(resultsPage.get().getResults().get(0).equals(company));
            Mockito.verify(companyDAO).findAllWithPaging(1, 10);

        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the getAllCompanies method work properly when the page number is incorrect.
     */
    @Test
    public void getAllCompaniesWithInvalidPageNumber() {
        try {
            Mockito.when(companyDAO.findAllWithPaging(-1, 10)).thenReturn(null);

            // get all companies with negative page number
            Optional<Page<Company>> resultsPageNegative = service.getAllCompaniesWithPaging(-1, 10);
            assertFalse(resultsPageNegative.isPresent());
            Mockito.verify(companyDAO).findAllWithPaging(-1, 10);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the getAllCompanies method work properly when the results per page is incorrect.
     */
    @Test
    public void getAllCompaniesWithInvalidResultsPerPage() {
        try {
            Mockito.when(companyDAO.findAllWithPaging(1, -1)).thenReturn(null);

            // get all companies with negative page number
            Optional<Page<Company>> resultsPageNegative = service.getAllCompaniesWithPaging(1, -1);
            assertFalse(resultsPageNegative.isPresent());
            Mockito.verify(companyDAO).findAllWithPaging(1, -1);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }

    /**
     * Test if the getAllCompanies method work properly when a SQL Exception is raised.
     */
    @Test
    public void getAllCompaniesWithSQLException() {
        try {
            Mockito.when(companyDAO.findAllWithPaging(1, 10)).thenThrow(SQLException.class);
            // Test SQL error
            Optional<Page<Company>> resultsPageException = service.getAllCompaniesWithPaging(1, 10);
            assertFalse(resultsPageException.isPresent());
            Mockito.verify(companyDAO).findAllWithPaging(1, 10);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
    }
}
