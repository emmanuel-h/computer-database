package main.java.com.excilys.cdb.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;

public class ComputerDAO implements DAO<Computer> {

	private Connection connection;
	private final String FIND_ALL_COMPUTERS = "SELECT * FROM computer ORDER BY computer.id";
	private final String FIND_ALL_MANUFACTURERS = "SELECT * FROM company, computer WHERE computer.company_id = company.id";
	
	public ComputerDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public List<Computer> findAll() throws SQLException {
		// First query to retrieve list of companies which are computer's manufacturers, in order to avoid duplicates.
		PreparedStatement statement_companies = connection.prepareStatement(FIND_ALL_MANUFACTURERS);
		List<Company> companies = new ArrayList<>();
		ResultSet rSet_companies = statement_companies.executeQuery();
		while(rSet_companies.next()){
			companies.add(new Company(rSet_companies.getInt("id"), rSet_companies.getString("name")));
		}
		// ResultSet and Statement are close in order to reuse them
		rSet_companies.close();
		statement_companies.close();
		
		// Second query which retrieve all computers
		List<Computer> computers = new ArrayList<>();
		PreparedStatement statement = connection.prepareStatement(FIND_ALL_COMPUTERS);
		ResultSet rs = statement.executeQuery();
		
		Company company;
	    while (rs.next()) {
	    	int company_id = rs.getInt("company_id");
	    	// The company is found from the companies list created before
	    	company = companies
	    			.stream()
	    			.filter(a -> a.getId() == company_id)
	    			.findFirst()
	    			.orElse(null);
	    	computers.add(new Computer(rs.getInt("computer.id"),rs.getString("computer.name"),
	    			rs.getDate("computer.introduced"), rs.getDate("computer.discontinued"),
	    			company));
	    }
		return computers;
	}

	@Override
	public Computer findOneById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Computer t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Computer t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Computer t) {
		// TODO Auto-generated method stub
		return false;
	}

}
