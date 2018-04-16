package main.java.com.excilys.cdb;

import main.java.com.excilys.cdb.daos.CompanyDAO;
import main.java.com.excilys.cdb.daos.CompanyDAOImpl;


public class Main {

	public static void main(String[] args) {
		CompanyDAO companyDAO = new CompanyDAOImpl();
		System.out.println(companyDAO.findOneById(1).getName());
	}
}
