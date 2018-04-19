package main.java.com.excilys.cdb.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.utils.Page;

public class CompanyDAO implements DAO<Company>{
	
	private Connection connection;
	private final String FIND_ALL_COMPANIES = "SELECT id, name FROM company LIMIT ?,?";
	private final String FIND_COMPANY_BY_ID = "SELECT id, name FROM company WHERE id=?";
	private final String ADD_COMPANY = "INSERT INTO company (id, name) VALUES (?, ?)";
	private final String DELETE_COMPANY = "DELETE FROM company WHERE id = ?";
	private final String UPDATE_COMPANY = "UPDATE company SET company.name = ? WHERE company.id = ?";
	private final String MAX_PAGE = "SELECT COUNT(id) FROM company";
	
	public CompanyDAO(Connection connection) {
		this.connection = connection;
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
	public Company findById(int id) throws SQLException {
		Company company = null;
		PreparedStatement statement = connection.prepareStatement(FIND_COMPANY_BY_ID);
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
		if(rs.next()){
			company = new Company(rs.getInt("id"), rs.getString("name"));
		}
		return company;
	}

	@Override
	public boolean add(Company company) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(ADD_COMPANY);
		statement.setInt(1, company.getId());
		statement.setString(2, company.getName());
		int result = statement.executeUpdate();
		if(result == 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean delete(Company company) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(DELETE_COMPANY);
		statement.setInt(1, company.getId());
		int result = statement.executeUpdate();
		if(result == 0) {
			return false;
		}
		return true;
		}

	@Override
	public boolean update(Company company) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(UPDATE_COMPANY);
		statement.setString(1, company.getName());
		statement.setInt(2, company.getId());
		int result = statement.executeUpdate();
		if(result == 0) {
			return false;
		}
		return true;
	}
}
