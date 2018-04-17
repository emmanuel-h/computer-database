package main.java.com.excilys.cdb;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import main.java.com.excilys.cdb.daos.CompanyDAO;
import main.java.com.excilys.cdb.daos.ComputerDAO;
import main.java.com.excilys.cdb.daos.DAOFactory;
import main.java.com.excilys.cdb.exceptions.FactoryException;
import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;


public class Main {

	public static void main(String[] args) {
		ComputerDAO computerDAO;
		try {
			/*CompanyDAO companyDAO = (CompanyDAO) DAOFactory.getDAO("company");
			Company company_test = new Company();
			company_test.setName("test");
			company_test.setId(43);
			//companyDAO.add(company_test);
			//company_test = companyDAO.findOneById(47);
			//companyDAO.delete(company_test);
			companyDAO.update(company_test);
			List<Company>companies =  companyDAO.findAll();
			for(Company company: companies) {
				System.out.println(company);
			}*/
			
			CompanyDAO companyDAO = (CompanyDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPANY);
			Company company_test = companyDAO.findById(1);
			computerDAO = (ComputerDAO) DAOFactory.getDAO(DAOFactory.DaoTypes.COMPUTER);
			Computer computer_test = new Computer(574, "test", new Date(), null, company_test);
			computerDAO.update(computer_test);
			List<Computer> computers = computerDAO.findAll();
			for(Computer computer : computers) {
				System.out.println(computer);
			}
			
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
