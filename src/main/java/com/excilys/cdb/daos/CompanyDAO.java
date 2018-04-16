package main.java.com.excilys.cdb.daos;

import java.util.List;

import main.java.com.excilys.cdb.model.Company;

public interface CompanyDAO {
	List<Company> findAll();
	Company findOneById(int id); 
}
