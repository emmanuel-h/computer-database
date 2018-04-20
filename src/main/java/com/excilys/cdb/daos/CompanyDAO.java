package main.java.com.excilys.cdb.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.utils.Page;

/**
 * ComputerDAo make the link between the database and the model
 * 
 * @author emmanuelh
 *
 */
public class CompanyDAO implements DAO<Company>{
	
	/**
	 * The connection to the database
	 */
	private Connection connection;
	
	/**
	 * The singleton's instance of CompanyDAO
	 */
	private static CompanyDAO companyDAO;
	
	private final String FIND_ALL_COMPANIES = "SELECT id, name FROM company LIMIT ?,?";
	private final String FIND_COMPANY_BY_ID = "SELECT id, name FROM company WHERE id=?";
	private final String ADD_COMPANY = "INSERT INTO company (id, name) VALUES (?, ?)";
	private final String DELETE_COMPANY = "DELETE FROM company WHERE id = ?";
	private final String UPDATE_COMPANY = "UPDATE company SET company.name = ? WHERE company.id = ?";
	private final String MAX_PAGE = "SELECT COUNT(id) FROM company";
	
	private CompanyDAO(Connection connection) {
		this.connection = connection;
	}
	
	public static CompanyDAO GetInstance(Connection connection) {
		if(null == companyDAO) {
			companyDAO = new CompanyDAO(connection);
		}
		return companyDAO;
	}

	@Override
	public Page<Company> findAll(int currentPage) throws SQLException {
		Page<Company> page = new Page<>();
		List<Company> companies = new ArrayList<>();
		PreparedStatement statement = connection.prepareStatement(FIND_ALL_COMPANIES);
		statement.setInt(1, (currentPage-1)*page.getResultsPerPage());
		statement.setInt(2, page.getResultsPerPage());
        ResultSet rs = statement.executeQuery();

	    while (rs.next()) {
	    	companies.add(new Company(rs.getInt("id"),rs.getString("name")));
	    }
	    
	    rs.close();
	    statement.close();
	    
	    // Count max pages
	    statement = connection.prepareStatement(MAX_PAGE);
	    rs = statement.executeQuery();
	    if(rs.next()) {
	    	page.setMaxResult(rs.getInt(1)/page.getResultsPerPage());
	    }
	    
	    page.setCurrentPage(currentPage);
	    page.setResults(companies);
	    return page;
	}

	@Override
	public Company findById(long l) throws SQLException {
		Company company = null;
		PreparedStatement statement = connection.prepareStatement(FIND_COMPANY_BY_ID);
        statement.setLong(1, l);
        ResultSet rs = statement.executeQuery();
		if(rs.next()){
			company = new Company(rs.getInt("id"), rs.getString("name"));
		}
		return company;
	}

	@Override
	public int add(Company company) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(ADD_COMPANY, Statement.RETURN_GENERATED_KEYS);
		statement.setLong(1, company.getId());
		statement.setString(2, company.getName());
		
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
		PreparedStatement statement = connection.prepareStatement(DELETE_COMPANY);
		statement.setLong(1, id);
		int result = statement.executeUpdate();
		if(result == 0) {
			return false;
		}
		return true;
		}

	@Override
	public Company update(Company company) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(UPDATE_COMPANY);
		statement.setString(1, company.getName());
		statement.setLong(2, company.getId());
		int result = statement.executeUpdate();
		if(result == 0) {
			return null;
		}
		return company;
	}
}
