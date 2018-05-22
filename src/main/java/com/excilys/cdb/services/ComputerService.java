package com.excilys.cdb.services;

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
        return Optional.ofNullable(this.computerDAO.findAllWithPaging(currentPage, maxResults));
    }

    /**
     * Retrieve a computer with his id.
     * @param id The id of the computer
     * @return The corresponding computer
     * @throws ComputerServiceException if there is no corresponding computer
     */
    public Computer getOneComputer(long id) throws ComputerServiceException {
        Optional<Computer> computerOptional = this.computerDAO.findById(id);
        if (!computerOptional.isPresent()) {
            throw new ComputerServiceException("The computer is not found");
        } else {
            return computerOptional.get();
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
        if (null != computer.getManufacturer()) {
            Optional<Company> company = companyService.getOneCompany(computer.getManufacturer().getId());
            if (!company.isPresent()) {
                throw new ComputerServiceException(UNKNOWN_MANUFACTURER);
            }
        }
        return this.computerDAO.add(computer);
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
        if (null != computer.getManufacturer() && computer.getManufacturer().getId() != 0) {
            Optional<Company> company = companyService.getOneCompany(computer.getManufacturer().getId());
            if (!company.isPresent()) {
                throw new ComputerServiceException(UNKNOWN_MANUFACTURER);
            }
        }
        try {
            return Optional.ofNullable(computerDAO.update(computer));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            LOGGER.warn("Problem when updating the Computer : " + e);
            throw new ComputerServiceException("Problem when updating the computer");
        }
    }

    /**
     * Delete an existing computer.
     * @param id The id of the computer to delete
     * @return true if the computer is deleted, false if not
     * @throws ComputerServiceException If there is no computer matching this id
     */
    public boolean deleteComputer(long id) {
        return computerDAO.delete(id);
    }

    /**
     * Count the number of existing computers.
     * @return  The number of computers
     */
    public int countComputers() {
        return computerDAO.count();
    }

    /**
     * Delete a list of computers.
     * @param toDelete  The list to delete
     * @return          True if the deletion completes, false if not
     */
    public boolean deleteMultipleComputers(String toDelete) {
        if (ComputersToDeleteValidator.verifyListToDelete(toDelete)) {
            return computerDAO.deleteMultiple(toDelete);
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
        return Optional.ofNullable(computerDAO.searchComputer(search, currentPage, maxResults));
    }

    /**
     * Count the number of computers corresponding to the search.
     * @param search    The String researched
     * @return          The number of computers
     */
    public int countSearchedComputers(String search) {
        return computerDAO.countSearchedComputers(search);
    }
}
