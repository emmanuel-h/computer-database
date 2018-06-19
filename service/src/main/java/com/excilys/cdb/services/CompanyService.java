package com.excilys.cdb.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.excilys.cdb.daos.CompanyDAO;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.Page;

@Service
public class CompanyService {

    private CompanyDAO companyDAO;

    /**
     * Constructor initializing the DAO.
     * @param companyDAO    The DAO regarding the company
     */
    private CompanyService(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }

    /**
     * Ask the company DAO to have a page of companies.
     * @param currentPage   The page number to have
     * @param maxResults    The number of items to display
     * @return              The page object corresponding to the criteria
     */
    public Optional<Page<Company>> getAllCompaniesWithPaging(int currentPage, int maxResults) {
        return Optional.ofNullable(this.companyDAO.findAllWithPaging(currentPage, maxResults));
    }

    /**
     * List all the existing companies.
     * @return  The list of companies
     */
    public List<Company> findAllCompanies() {
        return companyDAO.findAll();
    }

    /**
     * Delete a company and the associated computers.
     * @param id    The company identifier to delete
     * @return      True if the company has been successfully deleted, false if not
     */
    public boolean deleteCompany(long id) {
        return companyDAO.delete(id);
    }

    /**
     * Find a company with his identifier.
     * @param id    The identifier of the company
     * @return      The company found
     */
    public Optional<Company> getOneCompany(long id) {
        return companyDAO.findById(id);
    }
    
    /**
     * Create a company
     * @param company   The company model
     * @return          ID generated
     */
    public Long addCompany(final Company company) {
        return companyDAO.add(company);
    }
    
    /**
     * Update a company
     * @param company   The company model
     * @return          The company saved
     */
    public Company updateCompany(final Company company) {
        return companyDAO.update(company);
    }
    
    /**
     * Count the number of companies.
     * @return	The number of companies
     */
    public int countCompanies() {
    	return companyDAO.count();
    }

    /**
     * Search a company or a list of companies.
     * @param search        The name to search
     * @param currentPage   The page to display
     * @param maxResults    The number of results per page
     * @return              The result of the search
     */
    
    public Optional<Page<Company>> searchCompanies(String search, int currentPage, int maxResults) {
    	return Optional.ofNullable(companyDAO.searchCompany(search, currentPage, maxResults));
    }

    /**
     * Count the number of companies corresponding to the search.
     * @param search    The String researched
     * @return          The number of companies
     */
    public int countSearchedCompanies(String search) {
        return companyDAO.countSearchedCompanies(search);
    }
    
    /**
     * Count the number of computers with a certain manufacturer.
     * @param id	The id of the manufacturer
     * @return		The number of searched computers
     */
    public int countComputersOfCompany(long id) {
    	return companyDAO.countComputersOfCompany(id);
    }
}
