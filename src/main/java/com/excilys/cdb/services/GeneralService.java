package main.java.com.excilys.cdb.services;

import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.List;

import main.java.com.excilys.cdb.daos.CompanyDAO;
import main.java.com.excilys.cdb.daos.ComputerDAO;
import main.java.com.excilys.cdb.daos.DAOFactory;
import main.java.com.excilys.cdb.exceptions.FactoryException;
import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;

public class GeneralService {

	private CompanyDAO companyDAO;
	private ComputerDAO computerDAO;
	
	public GeneralService() {
		try {
			this.companyDAO = (CompanyDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPANY);
			this.computerDAO = (ComputerDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPUTER);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Computer> getAllComputers() throws GeneralSecurityException {
		try {
			return this.computerDAO.findAll();
		} catch (SQLException e) {
			throw new GeneralSecurityException("SQL Exception");
		}
	}

	public List<Company> getAllCompanies() throws GeneralSecurityException {
		try {
			return this.companyDAO.findAll();
		} catch (SQLException e) {
			throw new GeneralSecurityException("SQL Exception");
		}	
	}

	public Computer getOneComputer(int id) throws GeneralSecurityException {
		try {
			return this.computerDAO.findById(id);
		} catch (SQLException e) {
			throw new GeneralSecurityException("The computer is null");
		}
	}

	public boolean createComputer(Computer computer) throws GeneralSecurityException {
		if(null == computer) {
			throw new GeneralSecurityException("The computer is null");
		}
		if(null == computer.getName()) {
			throw new GeneralSecurityException("The computer does not have a name");
		}
		try {
			return this.computerDAO.add(computer);
		} catch (SQLException e) {
			throw new GeneralSecurityException("SQL Exception");
		}
	}

	public boolean updateComputer(Computer computer) throws GeneralSecurityException {
		if(null == computer) {
			throw new GeneralSecurityException("The computer is null");
		}
		if(null == computer.getName()) {
			throw new GeneralSecurityException("The computer does not have a name");
		}
		try {
			return computerDAO.update(computer);
		} catch (SQLException e) {
			throw new GeneralSecurityException("SQL Exception");
		}
	}

	public boolean deleteComputer(int id) throws GeneralSecurityException {
		Computer computer;
		try {
			computer = computerDAO.findById(id);
			if(null == computer) {
				throw new GeneralSecurityException("Id does not refer any computer");
			}
			return computerDAO.delete(computer);
		} catch (SQLException e) {
			throw new GeneralSecurityException("SQL Exception");
		}
	}
	
}
