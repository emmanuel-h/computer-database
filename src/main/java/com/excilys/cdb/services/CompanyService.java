package com.excilys.cdb.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.daos.CompanyDAO;
import com.excilys.cdb.daos.DAOFactory;
import com.excilys.cdb.exceptions.FactoryException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.utils.Page;

public class CompanyService {

    private static CompanyService service;

    private CompanyDAO companyDAO;

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(ComputerService.class);

    private final String SQL_EXCEPTION = "SQL Exception : ";

    /**
     * Constructor initializing the DAO.
     * @throws FactoryException  If the factory raise an exception
     */
    private CompanyService() throws FactoryException {
        this.companyDAO = (CompanyDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPANY);
    }

    /**
     * Initiate the singleton's instance of the CompanyService.
     * @return                   The singleton's instance of the CompanyService
     * @throws FactoryException  If an exception is raise during the service execution
     */
    public static CompanyService getInstance() throws FactoryException {
        if (null == service) {
            service = new CompanyService();
        }
        return service;
    }

    /**
     * Ask the company DAO to have a page of companies.
     * @param currentPage   The page number to have
     * @param maxResults    The number of items to display
     * @return              The page object corresponding to the criteria
     */
    public Optional<Page<Company>> getAllCompaniesWithPaging(int currentPage, int maxResults) {
        try {
            return Optional.ofNullable(this.companyDAO.findAllWithPaging(currentPage, maxResults));
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * List all the existing companies.
     * @return  The list of companies
     */
    public List<Company> findAllCompanies() {
        try {
            return companyDAO.findAll();
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return new ArrayList<Company>();
    }

    /**
     * Delete a company and the associated computers.
     * @param id    The company identifier to delete
     * @return      True if the company has been successfully deleted, false if not
     */
    public boolean deleteCompany(long id) {
        try {
            return companyDAO.delete(id);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return false;
    }

    /**
     * Find a company with his identifier.
     * @param id    The identifier of the company
     * @return      The company found
     */
    public Optional<Company> getOneCompany(long id) {
        try {
            return companyDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return Optional.empty();
    }
}
