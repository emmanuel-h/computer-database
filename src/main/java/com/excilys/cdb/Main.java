package main.java.com.excilys.cdb;

import java.sql.SQLException;

import main.java.com.excilys.cdb.daos.CompanyDAO;
import main.java.com.excilys.cdb.daos.DAOFactory;
import main.java.com.excilys.cdb.exceptions.FactoryException;


public class Main {

	public static void main(String[] args) {
		CompanyDAO companyDAO;
		try {
			companyDAO = (CompanyDAO) DAOFactory.getDAO("company");
			System.out.println(companyDAO.findOneById(1).getName());
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
