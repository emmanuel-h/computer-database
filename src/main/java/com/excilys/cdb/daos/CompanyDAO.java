package com.excilys.cdb.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.utils.Page;

/**
 * ComputerDAO make the link between the database and the model.
 * @author emmanuelh
 */
public class CompanyDAO implements DAO<Company> {

    /**
     * The connection to the database.
     */
    private Connection connection;

    /**
     * The singleton's instance of CompanyDAO.
     */
    private static CompanyDAO companyDAO;

    private final String FIND_ALL_COMPANIES = "SELECT id, name FROM company LIMIT ?,?";
    private final String FIND_COMPANY_BY_ID = "SELECT id, name FROM company WHERE id=?";
    private final String ADD_COMPANY = "INSERT INTO company (name) VALUES (?)";
    private final String DELETE_COMPANY = "DELETE FROM company WHERE id = ?";
    private final String UPDATE_COMPANY = "UPDATE company SET company.name = ? WHERE company.id = ?";
    private final String COUNT_COMPANIES = "SELECT COUNT(id) FROM company";

    /**
     * Default constructor with a Connection.
     * @param connection The connection
     */
    private CompanyDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Return the singleton's instance of companyDAO.
     * @param connection The database connection.
     * @return The companyDAO's instance
     */
    public static CompanyDAO getInstance(Connection connection) {
        if (null == companyDAO) {
            companyDAO = new CompanyDAO(connection);
        }
        return companyDAO;
    }

    @Override
    public Page<Company> findAll(int currentPage, int maxResults) throws SQLException {
        if (currentPage < 1 || maxResults < 1) {
            return null;
        }
        Page<Company> page = new Page<>();
        List<Company> companies = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement(FIND_ALL_COMPANIES);
        statement.setInt(1, (currentPage - 1) * page.getResultsPerPage());
        statement.setInt(2, page.getResultsPerPage());
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            companies.add(new Company(rs.getInt("id"), rs.getString("name")));
        }

        rs.close();
        statement.close();

        // Count max pages
        statement = connection.prepareStatement(COUNT_COMPANIES);
        rs = statement.executeQuery();
        if (rs.next()) {
            double maxPage = rs.getInt(1) / page.getResultsPerPage();
            page.setMaxPage((int) Math.ceil(maxPage));
        }

        page.setCurrentPage(currentPage);
        page.setResults(companies);
        return page;
    }

    @Override
    public Company findById(long id) throws SQLException {
        Company company = null;
        PreparedStatement statement = connection.prepareStatement(FIND_COMPANY_BY_ID);
        statement.setLong(1, id);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            company = new Company(rs.getInt("id"), rs.getString("name"));
        }
        return company;
    }

    @Override
    public long add(Company company) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(ADD_COMPANY, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, company.getName());

        // Execute the add request
        statement.executeUpdate();

        // Retrieve the id of the created object
        ResultSet rSet = statement.getGeneratedKeys();
        if (rSet.next()) {
            return rSet.getLong(1);
        }
        return 0;
    }

    @Override
    public boolean delete(long id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_COMPANY);
        statement.setLong(1, id);
        int result = statement.executeUpdate();
        if (result == 0) {
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
        if (result == 0) {
            return null;
        }
        return company;
    }
}
