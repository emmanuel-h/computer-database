package com.excilys.cdb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import com.excilys.cdb.daos.CompanyDAO;
import com.excilys.cdb.daos.ComputerDAO;
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

    /**
     * Initialize the mocks.
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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
        Mockito.when(companyDAO.findAllWithPaging(1, 10)).thenReturn(companies);

        Optional<Page<Company>> resultsPage = service.getAllCompaniesWithPaging(1, 10);
        assertTrue(resultsPage.get().getResults().get(0).equals(company));
        Mockito.verify(companyDAO).findAllWithPaging(1, 10);
    }

    /**
     * Test if the getAllCompanies method work properly when the page number is incorrect.
     */
    @Test
    public void getAllCompaniesWithInvalidPageNumber() {
        Mockito.when(companyDAO.findAllWithPaging(-1, 10)).thenReturn(null);
        Optional<Page<Company>> resultsPageNegative = service.getAllCompaniesWithPaging(-1, 10);
        assertFalse(resultsPageNegative.isPresent());
        Mockito.verify(companyDAO).findAllWithPaging(-1, 10);
    }

    /**
     * Test if the getAllCompanies method work properly when the results per page is incorrect.
     */
    @Test
    public void getAllCompaniesWithInvalidResultsPerPage() {
        Mockito.when(companyDAO.findAllWithPaging(1, -1)).thenReturn(null);
        Optional<Page<Company>> resultsPageNegative = service.getAllCompaniesWithPaging(1, -1);
        assertFalse(resultsPageNegative.isPresent());
        Mockito.verify(companyDAO).findAllWithPaging(1, -1);
    }

    /**
     * Test if the findAllCompanies method works properly.
     */
    @Test
    public void findAllCompanies() {
        Company company = new Company(1, "test");
        List<Company> companiesList = new ArrayList<>();
        companiesList.add(company);
        Mockito.when(companyDAO.findAll()).thenReturn(companiesList);
        List<Company> resultList = service.findAllCompanies();
        assertEquals(companiesList, resultList);
    }

    /**
     * Test if the legit company deletion works.
     */
    @Test
    public void deleteCompany() {
        Mockito.when(companyDAO.delete(1)).thenReturn(true);
        boolean test = service.deleteCompany(1);
        assertTrue(test);
    }

    /**
     * Test if the legit company search works.
     */
    @Test
    public void getOneCompany() {
        Company company = new Company(1, "test");
        Mockito.when(companyDAO.findById(1)).thenReturn(Optional.of(company));
        Optional<Company> result = service.getOneCompany(1);
        assertTrue(result.isPresent());
        assertEquals(result.get(), company);
    }
}
