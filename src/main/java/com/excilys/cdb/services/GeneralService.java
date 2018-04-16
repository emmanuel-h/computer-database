package main.java.com.excilys.cdb.services;

import java.util.List;

import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;

public interface GeneralService {
	
	List<Computer> getAllComputers();
	List<Company> getAllCompanies();
	Computer getOneComputer(int id);
	boolean createComputer(Computer computer);
	boolean updateComputer(Computer computer);
	boolean deleteComputer(int id);
}
