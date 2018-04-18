package main.java.com.excilys.cdb.services;

import java.sql.SQLException;
import java.util.List;

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
	
	public GeneralService() throws GeneralServiceException {
		try {
			this.companyDAO = (CompanyDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPANY);
			this.computerDAO = (ComputerDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPUTER);
		} catch (FactoryException e) {
			throw new GeneralServiceException("Erreur lors de la création de la factory. "+e.getMessage());
		}
	}
	
	public List<Computer> getAllComputers() throws GeneralServiceException {
		try {
			return this.computerDAO.findAll();
		} catch (SQLException e) {
			throw new GeneralServiceException("SQL Exception");
		}
	}

	public List<Company> getAllCompanies() throws GeneralServiceException {
		try {
			return this.companyDAO.findAll();
		} catch (SQLException e) {
			throw new GeneralServiceException("SQL Exception");
		}	
	}

	public Computer getOneComputer(int id) throws GeneralServiceException {
		try {
			return this.computerDAO.findById(id);
		} catch (SQLException e) {
			throw new GeneralServiceException("The computer is null");
		}
	}

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
			return this.computerDAO.add(computer);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneralServiceException("SQL Exception");
		}
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
			return computerDAO.update(computer);
		} catch (SQLException e) {
			throw new GeneralServiceException("SQL Exception");
		}
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
			throw new GeneralServiceException("SQL Exception");
		}
	}
	
}
