package com.excilys.cdb.services;

import java.util.Optional;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.excilys.cdb.daos.ComputerDAO;
import com.excilys.cdb.exceptions.company.CompanyUnknownException;
import com.excilys.cdb.exceptions.computer.ComputerException;
import com.excilys.cdb.exceptions.computer.ComputerNamelessException;
import com.excilys.cdb.exceptions.computer.ComputerNonExistentException;
import com.excilys.cdb.exceptions.computer.ComputerWithBadDatesException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.Page;
import com.excilys.cdb.validators.ComputerValidator;
import com.excilys.cdb.validators.ComputersToDeleteValidator;

@Service
public class ComputerService {
	private enum Sort_Allowed { NAME, INTRODUCED, DISCONTINUED, COMPANY };
	

    private ComputerDAO computerDAO;

    private CompanyService companyService;

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(ComputerService.class);

    private final String NULL_COMPUTER = "The computer is null";
    private final String UNNAMED_COMPUTER = "The computer does not have a name";
    private final String DATE_PROBLEM = "Discontinued date is before introduced date";
    private final String UNKNOWN_MANUFACTURER = "Manufacturer id unknown";
    private final String SQL_EXCEPTION = "SQL exception encountered";
    private final String ERROR_WHEN_UPDATE = "Problem when updating the Computer";

    /**
     * Constructor initializing the DAO.
     * @param computerDAO    The computer's DAO
     * @param companyService The service regarding the company
     */
    private ComputerService(ComputerDAO computerDAO, CompanyService companyService) {
        this.computerDAO = computerDAO;
        this.companyService = companyService;
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
    public Computer getOneComputer(long id) throws ComputerException {
        Optional<Computer> computerOptional = this.computerDAO.findById(id);
        if (!computerOptional.isPresent()) {
            throw new ComputerNonExistentException("The computer " + id + "is not found");
        } else {
            return computerOptional.get();
        }
    }

    /**
     * Ask the DAO to create a computer.
     * @param computer The computer to create
     * @return true if the computer is created, false if not
     * @throws CompanyUnknownException  If the manufacturer is unknown
     * @throws ComputerServiceException If the computer is null, doesn't have a name,
     *             dates are incorrect
     */
    public long createComputer(Computer computer) throws ComputerException, CompanyUnknownException {
        if (null == computer) {
            throw new ComputerNonExistentException(NULL_COMPUTER);
        }
        if (!ComputerValidator.validName(computer.getName())) {
            throw new ComputerNamelessException(UNNAMED_COMPUTER);
        }
        if (null != computer.getDiscontinued() && null != computer.getIntroduced()) {
            if (!ComputerValidator.discontinuedGreaterThanIntroduced(computer.getIntroduced(), computer.getDiscontinued())) {
                throw new ComputerWithBadDatesException(DATE_PROBLEM);
            }
        }
        if (null != computer.getManufacturer()) {
            Optional<Company> company = companyService.getOneCompany(computer.getManufacturer().getId());
            if (!company.isPresent()) {
                throw new CompanyUnknownException(UNKNOWN_MANUFACTURER);
            }
        }
        long id = -1;
        try {
        	id = this.computerDAO.add(computer);
        	if (id > 0 && null != computer.getManufacturer()) {
        		companyService.modifyNumberOfComputersOfCompany(true, computer.getManufacturer().getId());
        	}
        } catch (org.hibernate.exception.DataException e) {
        	throw new ComputerException(SQL_EXCEPTION);
        }
        return id;
    }

    /**
     * Update an existing computer.
     * @param computer The computer to update
     * @return true if the computer is updated, false if not
     * @throws CompanyUnknownException 
     * @throws ComputerServiceException If the computer is null, doesn't have a name,
     *             dates are incorrect or manufacturer is unknown
     */
    public Optional<Computer> updateComputer(Computer computer) throws ComputerException, CompanyUnknownException {
        if (null == computer) {
            throw new ComputerNonExistentException(NULL_COMPUTER);
        }
        if (!ComputerValidator.validName(computer.getName())) {
            throw new ComputerNamelessException(UNNAMED_COMPUTER);
        }
        if (null != computer.getDiscontinued() && null != computer.getIntroduced()) {
            if (!ComputerValidator.discontinuedGreaterThanIntroduced(computer.getIntroduced(), computer.getDiscontinued())) {
                throw new ComputerWithBadDatesException(DATE_PROBLEM);
            }
        }

        if (null != computer.getManufacturer() && computer.getManufacturer().getId() != 0) {
            Optional<Company> company = companyService.getOneCompany(computer.getManufacturer().getId());
            if (!company.isPresent()) {
                throw new CompanyUnknownException(UNKNOWN_MANUFACTURER);
            }
        }
        try {
        	Optional<Computer> computerTestManufacturer = computerDAO.findById(computer.getId());
            Optional<Computer> computerOptional = Optional.ofNullable(computerDAO.update(computer));

            // If there was a manufacturer in the updated computer and not in the initial one, increase
            // the company's number of computer
            if (computerOptional.isPresent()
            		&& computerTestManufacturer.isPresent()
            		&& null != computerOptional.get().getManufacturer()
            		&& null == computerTestManufacturer.get().getManufacturer()) {
            	companyService.modifyNumberOfComputersOfCompany(true, computerOptional.get().getManufacturer().getId());
            }
            // If there was a manufacturer in the initial computer and not in the updated one, decrease
            // the company's number of computer
            if (computerOptional.isPresent()
            		&& computerTestManufacturer.isPresent()
            		&& null == computerOptional.get().getManufacturer()
            		&& null != computerTestManufacturer.get().getManufacturer()) {
            	companyService.modifyNumberOfComputersOfCompany(false, computerTestManufacturer.get().getManufacturer().getId());
            }
            
            return computerOptional;
        } catch (RuntimeException e) {
            LOGGER.warn(ERROR_WHEN_UPDATE + " : " + e);
            throw new ComputerException(ERROR_WHEN_UPDATE);
        }
    }

    /**
     * Delete an existing computer.
     * @param id The id of the computer to delete
     * @return true if the computer is deleted, false if not
     * @throws CompanyUnknownException 
     * @throws ComputerServiceException If there is no computer matching this id
     */
    public boolean deleteComputer(long id) throws CompanyUnknownException {
    	Optional<Computer> computer = computerDAO.findById(id);
        boolean success = computerDAO.delete(id);
        if (computer.isPresent() && null != computer.get().getManufacturer()) {
        	companyService.modifyNumberOfComputersOfCompany(false, computer.get().getManufacturer().getId());
        }
        return success;
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
    
    public Optional<Page<Computer>> findAllWithPagingAndSorting(int currentPage, int maxResults, String sort, boolean asc) {
    	if("COMPANY".equals(sort.toUpperCase())) {
    		return Optional.ofNullable(computerDAO.findAllWithPagingAndSorting(currentPage, maxResults, "manufacturer.name", asc));
    	}
    	if(!EnumUtils.isValidEnum(Sort_Allowed.class, sort.toUpperCase())) {
    		return Optional.empty();
    	} else {
    		return Optional.ofNullable(computerDAO.findAllWithPagingAndSorting(currentPage, maxResults, sort.toLowerCase(), asc));
    	}
    }
    
    public Optional<Page<Computer>> findAllWithPagingAndSortingAndSearch(
    		String search, int currentPage, int maxResults, String sort, boolean asc) {
    	if("COMPANY".equals(sort.toUpperCase())) {
    		return Optional.ofNullable(computerDAO.findAllWithPagingAndSortingAndSearch(search, currentPage, maxResults, "manufacturer.name", asc));
    	}
    	if(!EnumUtils.isValidEnum(Sort_Allowed.class, sort.toUpperCase())) {
    		return Optional.empty();
    	} else {
    		return Optional.ofNullable(computerDAO.findAllWithPagingAndSortingAndSearch(search, currentPage, maxResults, sort, asc));
    	}
    }
}
