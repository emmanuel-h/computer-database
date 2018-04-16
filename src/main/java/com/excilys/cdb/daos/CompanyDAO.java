package main.java.com.excilys.cdb.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.excilys.cdb.model.Company;

public class CompanyDAO implements DAO<Company>{
	
	private Connection connection;
	
	public CompanyDAO(Connection connection) {
		this.connection = connection;
	}

	@Override
	public List<Company> findAll() throws SQLException {
		List<Company> companies = new ArrayList<>();
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM company");
        ResultSet rs = statement.executeQuery();

	    while (rs.next()) {
	    	companies.add(new Company(rs.getInt("id"),rs.getString("name")));
	    	System.out.println();
	    }
		return companies;
	}

	@Override
	public Company findOneById(int id) throws SQLException {
		Company company = null;
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM company WHERE id=?");
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
		if(rs.next()){
			company = new Company(rs.getInt("id"), rs.getString("name"));
		}
		return company;
	}

	@Override
	public boolean add(Company company) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("INSERT INTO company (id, name) VALUES (?, ?)");
		statement.setInt(1, company.getId());
		statement.setString(2, company.getName());
		ResultSet rSet = statement.executeQuery();
		return rSet.rowInserted();
	}

	@Override
	public boolean delete(Company t) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Company t) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}
