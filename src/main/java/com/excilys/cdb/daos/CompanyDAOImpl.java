package main.java.com.excilys.cdb.daos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sun.crypto.provider.RSACipher;

import main.java.com.excilys.cdb.model.Company;

public class CompanyDAOImpl implements CompanyDAO{
	 private Connection con;
	 Statement stmt;
	 
	public CompanyDAOImpl(){
	    try {
			con = DriverManager.getConnection(
			        "jdbc:mysql://localhost:3306/computer-database-db",
			        "admincdb",
			        "qwerty1234");
		    stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<Company> findAll() {
		List<Company> companies = new ArrayList<>();
		try {
			stmt.executeQuery("SELECT * FROM computer");
		    ResultSet rs = stmt.executeQuery("SELECT * FROM company");

		    while (rs.next()) {
		    	companies.add(new Company(rs.getInt("id"),rs.getString("name")));
		    	System.out.println();
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return companies;
	}

	@Override
	public Company findOneById(int id) {
		Company company = null;
		try {
			ResultSet rs = stmt.executeQuery("SELECT * FROM computer WHERE id =" + id);
			if(rs.next()){
				company = new Company(rs.getInt("id"), rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return company;
	}
	
	@Override
	protected void finalize() throws Throwable {
		con.close();
	}
}
