package com.excilys.cdb.services;

import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.excilys.cdb.daos.ComputerDAO;
import com.excilys.cdb.exceptions.ComputerServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.utils.Page;
import com.excilys.cdb.validators.ComputerValidator;
import com.excilys.cdb.validators.ComputersToDeleteValidator;

@Service
public class ComputerService {

    @Autowired
    private ComputerDAO computerDAO;

    @Autowired
    private CompanyService companyService;

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(ComputerService.class);

    private final String SQL_EXCEPTION = "SQL Exception : ";
    private final String NULL_COMPUTER = "The computer is null";
    private final String UNNAMED_COMPUTER = "The computer does not have a name";
    private final String DATE_PROBLEM = "Discontinued date is before introduced date";
    private final String UNKNOWN_MANUFACTURER = "Manufacturer id unknown";

    /**
     * Constructor initializing the DAO.
     */
    private ComputerService() {
    }

    /**
     * Ask the computer DAO to have a page of computers.
     * @param currentPage The page number to have
     * @param maxResults    The number of items to display
     * @return The page object corresponding to the criteria
     */
    public Optional<Page<Computer>> getAllComputersWithPaging(int currentPage, int maxResults) {
        try {
            return Optional.ofNullable(this.computerDAO.findAllWithPaging(currentPage, maxResults));
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieve a computer with his id.
     * @param id The id of the computer
     * @return The corresponding computer
     * @throws ComputerServiceException if there is no corresponding computer
     */
    public Computer getOneComputer(long id) throws ComputerServiceException {
        try {
            Optional<Computer> computerOptional =  this.computerDAO.findById(id);
            if (!computerOptional.isPresent()) {
                throw new ComputerServiceException("The computer is not found");
            } else {
                return computerOptional.get();
            }
        } catch (SQLException e) {
            throw new ComputerServiceException(NULL_COMPUTER);
        }
    }

    /**
     * Ask the DAO to create a computer.
     * @param computer The computer to create
     * @return true if the computer is created, false if not
     * @throws ComputerServiceException If the computer is null, doesn't have a name,
     *             dates are incorrect or manufacturer is unknown
     */
    public long createComputer(Computer computer) throws ComputerServiceException {
        if (null == computer) {
            throw new ComputerServiceException(NULL_COMPUTER);
        }
        if (!ComputerValidator.validName(computer.getName())) {
            throw new ComputerServiceException(UNNAMED_COMPUTER);
        }
        if (null != computer.getDiscontinued() && null != computer.getIntroduced()) {
            if (!ComputerValidator.discontinuedGreaterThanIntroduced(computer.getIntroduced(), computer.getDiscontinued())) {
                throw new ComputerServiceException(DATE_PROBLEM);
            }
        }
        try {
            if (null != computer.getManufacturer()) {
                Optional<Company> company = companyService.getOneCompany(computer.getManufacturer().getId());
                if (!company.isPresent()) {
                    throw new ComputerServiceException(UNKNOWN_MANUFACTURER);
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
     * @throws ComputerServiceException If the computer is null, doesn't have a name,
     *             dates are incorrect or manufacturer is unknown
     */
    public Optional<Computer> updateComputer(Computer computer) throws ComputerServiceException {
        if (null == computer) {
            throw new ComputerServiceException(NULL_COMPUTER);
        }
        if (!ComputerValidator.validName(computer.getName())) {
            throw new ComputerServiceException(UNNAMED_COMPUTER);
        }
        if (null != computer.getDiscontinued() && null != computer.getIntroduced()) {
            if (!ComputerValidator.discontinuedGreaterThanIntroduced(computer.getIntroduced(), computer.getDiscontinued())) {
                throw new ComputerServiceException(DATE_PROBLEM);
            }
        }
        try {
            if (null != computer.getManufacturer() && computer.getManufacturer().getId() != 0) {
                Optional<Company> company = companyService.getOneCompany(computer.getManufacturer().getId());
                if (!company.isPresent()) {
                    throw new ComputerServiceException(UNKNOWN_MANUFACTURER);
                }
            }
            return Optional.ofNullable(computerDAO.update(computer));
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Delete an existing computer.
     * @param id The id of the computer to delete
     * @return true if the computer is deleted, false if not
     * @throws ComputerServiceException If there is no computer matching this id
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

    /**
     * Delete a list of computers.
     * @param toDelete  The list to delete
     * @return          True if the deletion completes, false if not
     */
    public boolean deleteMultipleComputers(String toDelete) {
        if (ComputersToDeleteValidator.verifyListToDelete(toDelete)) {
            try {
                return computerDAO.deleteMultiple(toDelete);
            } catch (SQLException e) {
                LOGGER.warn(SQL_EXCEPTION + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Search a computer or a list of computers.
     * @param search        The name to search
     * @param currentPage   The page to display
     * @param maxResults    The number of results per page
     * @return              The result of the search
     */
    public Optional<Page<Computer>> searchComputer(String search, int currentPage, int maxResults) {
        try {
            return Optional.ofNullable(computerDAO.searchComputer(search, currentPage, maxResults));
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Count the number of computers corresponding to the search.
     * @param search    The String researched
     * @return          The number of computers
     */
    public int countSearchedComputers(String search) {
        try {
            return computerDAO.countSearchedComputers(search);
        } catch (SQLException e) {
            LOGGER.warn(SQL_EXCEPTION + e.getMessage());
        }
        return -1;
    }
}
