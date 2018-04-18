package main.java.com.excilys.cdb.services;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.cdb.daos.CompanyDAO;
import main.java.com.excilys.cdb.daos.ComputerDAO;
import main.java.com.excilys.cdb.daos.DAOFactory;
import main.java.com.excilys.cdb.exceptions.FactoryException;
import main.java.com.excilys.cdb.exceptions.GeneralServiceException;
import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;

public class GeneralService {

	private CompanyDAO companyDAO;
	private ComputerDAO computerDAO;
	private final Logger LOGGER = LoggerFactory.getLogger(GeneralService.class);
	
	public GeneralService() throws GeneralServiceException {
		try {
			this.companyDAO = (CompanyDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPANY);
			this.computerDAO = (ComputerDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPUTER);
		} catch (FactoryException e) {
			LOGGER.warn("Erreur lors de la conception de la factory : "+e.getMessage());
		}
	}
	
	public List<Computer> getAllComputers() {
		try {
			return this.computerDAO.findAll();
		} catch (SQLException e) {
			LOGGER.warn("SQL Exception : "+e.getMessage());
		}
		return null;
	}

	public List<Company> getAllCompanies() {
		try {
			return this.companyDAO.findAll();
		} catch (SQLException e) {
			LOGGER.warn("SQL Exception : "+e.getMessage());
		}
		return null;
	}

	public Computer getOneComputer(int id) throws GeneralServiceException {
		try {
			return this.computerDAO.findById(id);
		} catch (SQLException e) {
			throw new GeneralServiceException("The computer is null");
		}
	}

	//TODO: factorize create and update
	public boolean createComputer(Computer computer) throws GeneralServiceException {
		if(null == computer) {
			throw new GeneralServiceException("The computer is null");
		}
		if(null == computer.getName()) {
			throw new GeneralServiceException("The computer does not have a name");
		}
		if(computer.getDiscontinued().before(computer.getIntroduced())) {
			throw new GeneralServiceException("Discontinued date is before introduced date");
		}
		try {
			if(computer.getId() != 0) {
				Company company = companyDAO.findById(computer.getId());
				if (null == company) {
					throw new GeneralServiceException("Manufacturer id unknown");
				}
			}
			return this.computerDAO.add(computer);
		} catch (SQLException e) {
			LOGGER.warn("SQL Exception : "+e.getMessage());
		}
		return false;
	}

	public boolean updateComputer(Computer computer) throws GeneralServiceException {
		if(null == computer) {
			throw new GeneralServiceException("The computer is null");
		}
		if(null == computer.getName()) {
			throw new GeneralServiceException("The computer does not have a name");
		}
		if(computer.getDiscontinued().before(computer.getIntroduced())) {
			throw new GeneralServiceException("Discontinued date is before introduced date");
		}
		try {
			if(computer.getId() != 0) {
				Company company = companyDAO.findById(computer.getId());
				if (null == company) {
					throw new GeneralServiceException("Manufacturer id unknown");
				}
			}
			return computerDAO.update(computer);
		} catch (SQLException e) {
			LOGGER.warn("SQL Exception : "+e.getMessage());
		}
		return false;
	}

	public boolean deleteComputer(int id) throws GeneralServiceException {
		Computer computer;
		try {
			computer = computerDAO.findById(id);
			if(null == computer) {
				throw new GeneralServiceException("Id does not refer any computer");
			}
			return computerDAO.delete(computer);
		} catch (SQLException e) {
			LOGGER.warn("SQL Exception : "+e.getMessage());
		}
		return false;
	}
	
}
