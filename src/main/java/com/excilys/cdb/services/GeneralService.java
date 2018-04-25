package com.excilys.cdb.services;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.daos.CompanyDAO;
import com.excilys.cdb.daos.ComputerDAO;
import com.excilys.cdb.daos.DAOFactory;
import com.excilys.cdb.exceptions.FactoryException;
import com.excilys.cdb.exceptions.GeneralServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.utils.Page;

public class GeneralService {

    /**
     * The singleton's instance of the service.
     */
    private static GeneralService generalService;

    /**
     * The company concerning DAO.
     */
    private CompanyDAO companyDAO;

    /**
     * The computer concerning DAO.
     */
    private ComputerDAO computerDAO;

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(GeneralService.class);

    private final String SQL_EXCEPTION = "SQL Exception : ";
    private final String NULL_COMPUTER = "The computer is null";
    private final String ERREUR_FACTORY = "Erreur lors de la conception de la factory : ";
    private final String UNNAMED_COMPUTER = "The computer does not have a name";
    private final String DATE_PROBLEM = "Discontinued date is before introduced date";
    private final String UNKNOWN_MANUFACTURER = "Manufacturer id unknown";

    /**
     * Constructor initializing the DAOs.
     * @throws GeneralServiceException  If the factory raise an exception
     */
    private GeneralService() throws GeneralServiceException {
        try {
            this.companyDAO = (CompanyDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPANY);
            this.computerDAO = (ComputerDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPUTER);
        } catch (FactoryException e) {
            LOGGER.warn(ERREUR_FACTORY + e.getMessage());
        }
    }

    /**
     * Initiate the singleton's instance of the GeneralService.
     * @return                          The singleton's instance of the GeneralService
     * @throws GeneralServiceException  If an exception is raise during the service execution
     */
    public static GeneralService getInstance() throws GeneralServiceException {
        if (null == generalService) {
            generalService = new GeneralService();
        }
        return generalService;
    }

    /**
     * Ask the computer DAO to have a page of computers.
     * @param currentPage The page number to have
     * @param maxResults    The number of items to display
     * @return The page object corresponding to the criteria
     */
    public Page<Computer> getAllComputers(int currentPage, int maxResults) {
        try {
            return this.computerDAO.findAll(currentPage, maxResults);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return null;
    }

    /**
     * Ask the company DAO to have a page of companies.
     * @param currentPage   The page number to have
     * @param maxResults    The number of items to display
     * @return              The page object corresponding to the criteria
     */
    public Page<Company> getAllCompanies(int currentPage, int maxResults) {
        try {
            return this.companyDAO.findAll(currentPage, maxResults);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieve a computer with his id.
     * @param id The id of the computer
     * @return The corresponding computer
     * @throws GeneralServiceException if there is no corresponding computer
     */
    public Computer getOneComputer(long id) throws GeneralServiceException {
        try {
            return this.computerDAO.findById(id);
        } catch (SQLException e) {
            throw new GeneralServiceException(NULL_COMPUTER);
        }
    }

    /**
     * Ask the DAO to create a computer.
     * @param computer The computer to create
     * @return true if the computer is created, false if not
     * @throws GeneralServiceException If the computer is null, doesn't have a name,
     *             dates are incorrect or manufacturer is unknown
     */
    public long createComputer(Computer computer) throws GeneralServiceException {
        if (null == computer) {
            throw new GeneralServiceException(NULL_COMPUTER);
        }
        if (null == computer.getName()) {
            throw new GeneralServiceException(UNNAMED_COMPUTER);
        }
        if (null != computer.getDiscontinued() && null != computer.getIntroduced()) {
            if (computer.getDiscontinued().isBefore(computer.getIntroduced())) {
                throw new GeneralServiceException(DATE_PROBLEM);
            }
        }
        try {
            if (null != computer.getManufacturer()) {
                Company company = companyDAO.findById(computer.getManufacturer().getId());
                if (null == company) {
                    throw new GeneralServiceException(UNKNOWN_MANUFACTURER);
                }
            }
            return this.computerDAO.add(computer);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return 0L;
    }

    /**
     * Update an existing computer.
     * @param computer The computer to update
     * @return true if the computer is updated, false if not
     * @throws GeneralServiceException If the computer is null, doesn't have a name,
     *             dates are incorrect or manufacturer is unknown
     */
    public Computer updateComputer(Computer computer) throws GeneralServiceException {
        if (null == computer) {
            throw new GeneralServiceException(NULL_COMPUTER);
        }
        if (null == computer.getName()) {
            throw new GeneralServiceException(UNNAMED_COMPUTER);
        }
        if (null != computer.getDiscontinued() && null != computer.getIntroduced()) {
            if (computer.getDiscontinued().isBefore(computer.getIntroduced())) {
                throw new GeneralServiceException(DATE_PROBLEM);
            }
        }
        try {
            if (null != computer.getManufacturer() && computer.getManufacturer().getId() != 0) {
                Company company = companyDAO.findById(computer.getManufacturer().getId());
                if (null == company) {
                    throw new GeneralServiceException(UNKNOWN_MANUFACTURER);
                }
            }
            return computerDAO.update(computer);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return null;
    }

    /**
     * Delete an existing computer.
     * @param id The id of the computer to delete
     * @return true if the computer is deleted, false if not
     * @throws GeneralServiceException If there is no computer matching this id
     */
    public boolean deleteComputer(long id) {
        try {
            return computerDAO.delete(id);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return false;
    }

    /**
     * Count the number of existing computers.
     * @return  The number of computers
     */
    public int countComputers() {
        try {
            return computerDAO.count();
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return -1;
    }

}
