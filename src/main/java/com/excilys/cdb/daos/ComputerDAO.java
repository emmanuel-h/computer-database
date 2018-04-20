package com.excilys.cdb.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.utils.DateConvertor;
import com.excilys.cdb.utils.Page;

/**
 * ComputerDAo make the link between the database and the model
 * 
 * @author emmanuelh
 *
 */
public class ComputerDAO implements DAO<Computer> {

	/**
	 * The connection to the database
	 */
	private Connection connection;
	
	/**
	 * The singleton's instance of ComputerDAO
	 */
	private static ComputerDAO computerDAO;
	
	private final String FIND_ALL_COMPUTERS = "SELECT id, name, introduced, discontinued, company_id FROM computer LIMIT ?,?";
	private final String FIND_ALL_MANUFACTURERS = "SELECT company.id, company.name FROM company, computer WHERE computer.company_id = company.id";
	private final String FIND_COMPUTER_BY_ID = "SELECT id, name, introduced, discontinued, company_id FROM computer WHERE id = ?";
	private final String FIND_MANUFACTURER_BY_ID = "SELECT id, name FROM company WHERE id = ?";
	private final String ADD_COMPUTER = "INSERT INTO computer (name, introduced, discontinued, company_id)"
			+ "VALUES (?, ?, ?, ?)";
	private final String DELETE_COMPUTER = "DELETE FROM computer WHERE id = ?";
	private final String UPDATE_COMPUTER = "UPDATE computer SET name = ?, introduced = ?, "
			+ "discontinued = ?, company_id= ? WHERE computer.id = ?";
	
	private ComputerDAO(Connection connection) {
		this.connection = connection;
	}
	
	public static ComputerDAO GetInstance(Connection connection) {
		if(null == computerDAO) {
			computerDAO = new ComputerDAO(connection);
		}
		return computerDAO;
	}

	@Override
	public Page<Computer> findAll(int currentPage) throws SQLException {
		// First query to retrieve list of companies which are computer's manufacturers, in order to avoid duplicates.
		PreparedStatement statement_companies = connection.prepareStatement(FIND_ALL_MANUFACTURERS);
		List<Company> companies = new ArrayList<>();
		Page<Computer>page = new Page<>();
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
		statement.setInt(1, (currentPage-1)*page.getResultsPerPage());
		statement.setInt(2, page.getResultsPerPage());
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
	    	computers.add(new Computer.Builder(rs.getString("computer.name"))
	    			.id(rs.getInt("computer.id"))
	    			.introduced(DateConvertor.TimeStampToLocalDate(rs.getTimestamp("computer.introduced")))
	    			.discontinued(DateConvertor.TimeStampToLocalDate(rs.getTimestamp("computer.discontinued")))
	    			.manufacturer(company)
	    			.build());
	    }
	    page.setCurrentPage(currentPage);
	    page.setResults(computers);
	    return page;
	}

	@Override
	public Computer findById(long id) throws SQLException {
		Computer computer = null;
		PreparedStatement statement = connection.prepareStatement(FIND_COMPUTER_BY_ID);
		statement.setLong(1, id);
		ResultSet rSet = statement.executeQuery();
		int company_id = 0;
		if(rSet.next()) {
			computer = new Computer.Builder(rSet.getString("name"))
					.id(rSet.getInt("id"))
					.introduced(DateConvertor.TimeStampToLocalDate(rSet.getTimestamp("introduced")))
					.discontinued(DateConvertor.TimeStampToLocalDate(rSet.getTimestamp("discontinued")))
	    			.build();
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
	public int add(Computer computer) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(ADD_COMPUTER, Statement.RETURN_GENERATED_KEYS);

		Timestamp introducedSQL = null;
		Timestamp discontinuedSQL = null;
		
		// Check if the dates are null or not
		if(null != computer.getIntroduced()) {
			introducedSQL = DateConvertor.LocalDateToTimeStamp(computer.getIntroduced());
		}
		if(null != computer.getDiscontinued()) {
			discontinuedSQL = DateConvertor.LocalDateToTimeStamp(computer.getDiscontinued());
		}
		
		// Give the statement parameters
		statement.setString(1, computer.getName());
		statement.setTimestamp(2, introducedSQL);
		statement.setTimestamp(3, discontinuedSQL);
		if(null == computer.getManufacturer()) {
			statement.setObject(4, null);
		} else {
			statement.setLong(4, computer.getManufacturer().getId());
		}
		
		// Execute the add request
		statement.executeUpdate();

		// Retrieve the id of the created object
		ResultSet rSet = statement.getGeneratedKeys();
		if(rSet.next()) {
			return rSet.getInt(1);
		}
		return 0;
	}

	@Override
	public boolean delete(long id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(DELETE_COMPUTER);
		statement.setLong(1, id);
		int result = statement.executeUpdate();
		if(result == 0) {
			return false;
		}
		return true;
	}

	@Override
	public Computer update(Computer computer) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(UPDATE_COMPUTER);
		
		Timestamp introducedSQL = null;
		Timestamp discontinuedSQL = null;
		
		// Check if the dates are null or not
		if(null != computer.getIntroduced()) {
			introducedSQL = DateConvertor.LocalDateToTimeStamp(computer.getIntroduced());
		}
		if(null != computer.getDiscontinued()) {
			discontinuedSQL = DateConvertor.LocalDateToTimeStamp(computer.getDiscontinued());
		}
		
		// Give the statement parameters
		statement.setString(1, computer.getName());
		statement.setTimestamp(2, introducedSQL);
		statement.setTimestamp(3, discontinuedSQL);
		if(null == computer.getManufacturer()) {
			statement.setObject(4, null);
		} else {
			statement.setLong(4, computer.getManufacturer().getId());
		}
		statement.setLong(5, computer.getId());
		
		int result = statement.executeUpdate();
		if(result == 0) {
			return null;
		}
		return computer;
	}
}
