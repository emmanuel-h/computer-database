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
	private final String FIND_ALL_COMPUTERS = "SELECT id, name, introduced, discontinued, company_id FROM computer ORDER BY computer.id";
	private final String FIND_ALL_MANUFACTURERS = "SELECT company.id, company.name FROM company, computer WHERE computer.company_id = company.id";
	private final String FIND_COMPUTER_BY_ID = "SELECT id, name, introduced, discontinued, company_id FROM computer WHERE id = ?";
	private final String FIND_MANUFACTURER_BY_ID = "SELECT id, name FROM company WHERE id = ?";
	private final String ADD_COMPUTER = "INSERT INTO computer (name, introduced, discontinued, company_id)"
			+ "VALUES (?, ?, ?, ?)";
	private final String DELETE_COMPUTER = "DELETE FROM computer WHERE id = ?";
	private final String UPDATE_COMPUTER = "UPDATE computer SET name = ?, introduced = ?, "
			+ "discontinued = ?, company_id= ? WHERE computer.id = ?";
	
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
	public Computer findById(int id) throws SQLException {
		Computer computer = null;
		PreparedStatement statement = connection.prepareStatement(FIND_COMPUTER_BY_ID);
		statement.setInt(1, id);
		ResultSet rSet = statement.executeQuery();
		int company_id = 0;
		if(rSet.next()) {
			computer = new Computer(rSet.getInt("id"),rSet.getString("name"),
	    			rSet.getDate("introduced"), rSet.getDate("discontinued"), null);
			company_id = rSet.getInt("company_id");
		}
		rSet.close();
		statement.close();
		Company company = null;
		if(company_id != 0) {
			PreparedStatement statement_company = connection.prepareStatement(FIND_MANUFACTURER_BY_ID);
			statement_company.setInt(1, company_id);
			ResultSet rSet_company = statement_company.executeQuery();
			if(rSet_company.next()) {
				company = new Company(company_id, rSet_company.getString("name"));
			}
		}
		computer.setManufacturer(company);
		return computer;
	}

	@Override
	public boolean add(Computer computer) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(ADD_COMPUTER);

		java.sql.Date introducedSQL = null;
		java.sql.Date discontinuedSQL = null;
		
		// Check if the dates are null or not
		if(null != computer.getIntroduced()) {
			introducedSQL = new java.sql.Date(computer.getIntroduced().getTime());
		}
		if(null != computer.getDiscontinued()) {
			discontinuedSQL = new java.sql.Date(computer.getDiscontinued().getTime());
		}
		
		// Give the statement parameters
		statement.setString(1, computer.getName());
		statement.setDate(2, introducedSQL);
		statement.setDate(3, discontinuedSQL);
		if(null == computer.getManufacturer()) {
			statement.setObject(4, null);
		} else {
			statement.setInt(4, computer.getManufacturer().getId());
		}
		
		int result = statement.executeUpdate();
		if(result == 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean delete(Computer computer) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(DELETE_COMPUTER);
		statement.setInt(1, computer.getId());
		int result = statement.executeUpdate();
		if(result == 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean update(Computer computer) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(UPDATE_COMPUTER);
		
		java.sql.Date introducedSQL = null;
		java.sql.Date discontinuedSQL = null;
		
		// Check if the dates are null or not
		if(null != computer.getIntroduced()) {
			introducedSQL = new java.sql.Date(computer.getIntroduced().getTime());
		}
		if(null != computer.getDiscontinued()) {
			discontinuedSQL = new java.sql.Date(computer.getDiscontinued().getTime());
		}
		
		// Give the statement parameters
		statement.setString(1, computer.getName());
		statement.setDate(2, introducedSQL);
		statement.setDate(3, discontinuedSQL);
		if(null == computer.getManufacturer()) {
			statement.setObject(4, null);
		} else {
			statement.setInt(4, computer.getManufacturer().getId());
		}
		statement.setInt(5, computer.getId());
		
		int result = statement.executeUpdate();
		if(result == 0) {
			return false;
		}
		return true;
	}
}
