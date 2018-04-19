package main.java.com.excilys.cdb.services;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.cdb.daos.CompanyDAO;
import main.java.com.excilys.cdb.daos.ComputerDAO;
import main.java.com.excilys.cdb.daos.DAOFactory;
import main.java.com.excilys.cdb.exceptions.FactoryException;
import main.java.com.excilys.cdb.exceptions.GeneralServiceException;
import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;
import main.java.com.excilys.cdb.utils.Page;

public class GeneralService {

	private final String SQL_EXCEPTION = "SQL Exception : ";
	private final String NULL_COMPUTER = "The computer is null";
	private final String ERREUR_FACTORY = "Erreur lors de la conception de la factory : ";
	private final String UNNAMED_COMPUTER = "The computer does not have a name";
	private final String DATE_PROBLEM = "Discontinued date is before introduced date";
	private final String UNKNOWN_MANUFACTURER = "Manufacturer id unknown";
	private final String UNKNOWN_COMPUTER = "Id does not refer any computer";
	
	private CompanyDAO companyDAO;
	private ComputerDAO computerDAO;
	private final Logger LOGGER = LoggerFactory.getLogger(GeneralService.class);
	
	public GeneralService() throws GeneralServiceException {
		try {
			this.companyDAO = (CompanyDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPANY);
			this.computerDAO = (ComputerDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPUTER);
		} catch (FactoryException e) {
			LOGGER.warn(ERREUR_FACTORY+e.getMessage());
		}
	}
	
	public Page<Computer> getAllComputers(int currentPage) {
		try {
			return this.computerDAO.findAll(currentPage);
		} catch (SQLException e) {
			LOGGER.warn(SQL_EXCEPTION+e.getMessage());
		}
		return null;
	}

	public Page<Company> getAllCompanies(int currentPage) {
		try {
			
			return this.companyDAO.findAll(currentPage);
		} catch (SQLException e) {
			LOGGER.warn(SQL_EXCEPTION+e.getMessage());
		}
		return null;
	}

	public Computer getOneComputer(int id) throws GeneralServiceException {
		try {
			return this.computerDAO.findById(id);
		} catch (SQLException e) {
			throw new GeneralServiceException(NULL_COMPUTER);
		}
	}

	//TODO: factorize create and update
	public boolean createComputer(Computer computer) throws GeneralServiceException {
		if(null == computer) {
			throw new GeneralServiceException(NULL_COMPUTER);
		}
		if(null == computer.getName()) {
			throw new GeneralServiceException(UNNAMED_COMPUTER);
		}
		if(computer.getDiscontinued().before(computer.getIntroduced())) {
			throw new GeneralServiceException(DATE_PROBLEM);
		}
		try {
			if(computer.getId() != 0) {
				Company company = companyDAO.findById(computer.getId());
				if (null == company) {
					throw new GeneralServiceException(UNKNOWN_MANUFACTURER);
				}
			}
			return this.computerDAO.add(computer);
		} catch (SQLException e) {
			LOGGER.warn(SQL_EXCEPTION+e.getMessage());
		}
		return false;
	}

	public boolean updateComputer(Computer computer) throws GeneralServiceException {
		if(null == computer) {
			throw new GeneralServiceException(NULL_COMPUTER);
		}
		if(null == computer.getName()) {
			throw new GeneralServiceException(UNNAMED_COMPUTER);
		}
		if(computer.getDiscontinued().before(computer.getIntroduced())) {
			throw new GeneralServiceException(DATE_PROBLEM);
		}
		try {
			if(computer.getId() != 0) {
				Company company = companyDAO.findById(computer.getId());
				if (null == company) {
					throw new GeneralServiceException(UNKNOWN_MANUFACTURER);
				}
			}
			return computerDAO.update(computer);
		} catch (SQLException e) {
			LOGGER.warn(SQL_EXCEPTION+e.getMessage());
		}
		return false;
	}

	public boolean deleteComputer(int id) throws GeneralServiceException {
		Computer computer;
		try {
			computer = computerDAO.findById(id);
			if(null == computer) {
				throw new GeneralServiceException(UNKNOWN_COMPUTER);
			}
			return computerDAO.delete(computer);
		} catch (SQLException e) {
			LOGGER.warn(SQL_EXCEPTION+e.getMessage());
		}
		return false;
	}
	
}
