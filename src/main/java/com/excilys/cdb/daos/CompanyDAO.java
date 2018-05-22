package com.excilys.cdb.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.utils.Page;

/**
 * ComputerDAO make the link between the database and the model.
 * @author emmanuelh
 */
@Repository
public class CompanyDAO implements DAO<Company> {

    @Autowired
    private DataSource dataSource;

    private final String FIND_ALL_COMPANIES = "SELECT id, name FROM company";
    private final String FIND_ALL_COMPANIES_WITH_PAGING = "SELECT id, name FROM company LIMIT ?,?";
    private final String FIND_COMPANY_BY_ID = "SELECT id, name FROM company WHERE id=?";
    private final String ADD_COMPANY = "INSERT INTO company (name) VALUES (?)";
    private final String DELETE_COMPANY = "DELETE FROM company WHERE id = ?";
    private final String UPDATE_COMPANY = "UPDATE company SET company.name = ? WHERE company.id = ?";
    private final String COUNT_COMPANIES = "SELECT COUNT(id) FROM company";
    private final String DELETE_COMPUTER_FROM_MANUFACTURER = "DELETE FROM computer WHERE computer.company_id = ?";

    /**
     * A logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(CompanyDAO.class);

    /**
     * Default constructor with a Connection.
     */
    private CompanyDAO() {
    }

    @Override
    public Page<Company> findAllWithPaging(int currentPage, int maxResults) throws SQLException {
        if (currentPage < 1 || maxResults < 1) {
            return null;
        }
        Page<Company> page = new Page<>();
        List<Company> companies = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
                PreparedStatement statement = con.prepareStatement(FIND_ALL_COMPANIES_WITH_PAGING)) {
            statement.setInt(1, (currentPage - 1) * page.getResultsPerPage());
            statement.setInt(2, page.getResultsPerPage());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    companies.add(new Company(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        try (Connection con = dataSource.getConnection();
                PreparedStatement statement = con.prepareStatement(COUNT_COMPANIES);
                ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
                double maxPage = (double) rs.getInt(1) / page.getResultsPerPage();
                page.setMaxPage((int) Math.ceil(maxPage));
            }

            page.setCurrentPage(currentPage);
            page.setResults(companies);
        }
        return page;
    }

    @Override
    public Optional<Company> findById(long id) throws SQLException {
        Optional<Company> company = Optional.empty();
        try (Connection con = dataSource.getConnection();
                PreparedStatement statement = con.prepareStatement(FIND_COMPANY_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    company = Optional.of(new Company(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return company;
    }

    @Override
    public long add(Company company) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(ADD_COMPANY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, company.getName());
            statement.executeUpdate();
            try (ResultSet rSet = statement.getGeneratedKeys()) {
                if (rSet.next()) {
                    return rSet.getLong(1);
                }
            }
        }
        return 0;
    }

    @Override
    public boolean delete(long id) throws SQLException {
        boolean result = false;
        Connection connection = dataSource.getConnection();
        PreparedStatement statementCompany = connection.prepareStatement(DELETE_COMPANY);
        PreparedStatement statementComputers = connection.prepareStatement(DELETE_COMPUTER_FROM_MANUFACTURER);
        try {
            connection.setAutoCommit(false);
            statementCompany.setLong(1, id);
            statementComputers.setLong(1, id);
            statementComputers.executeUpdate();
            int resultChange = statementCompany.executeUpdate();
            if (resultChange != 0) {
                result = true;
            }
            connection.commit();
        } catch (SQLException e) {
            result = false;
            LOGGER.warn("SQL exception when deleting a company : " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException exception) {
                LOGGER.warn("SQL exception when deleting a company after a rollback : " + exception.getMessage());
            }
        } finally {
            if (null != statementCompany) {
                statementCompany.close();
            }
            if (null != statementComputers) {
                statementComputers.close();
            }
            connection.setAutoCommit(true);
        }
        return result;
    }

    @Override
    public Company update(Company company) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE_COMPANY)) {
            statement.setString(1, company.getName());
            statement.setLong(2, company.getId());
            int result = statement.executeUpdate();
            if (result == 0) {
                return null;
            }
        }
        return company;
    }

    /**
     * Find all companies.
     * @return The list of companies, or an empty List if there is no one
     * @throws SQLException If there is a problem with the SQL request
     */
    public List<Company> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_COMPANIES);
                ResultSet rs = statement.executeQuery()) {
            List<Company> companies = new ArrayList<>();
            while (rs.next()) {
                companies.add(new Company(rs.getInt("id"), rs.getString("name")));
            }
            return companies;
        }
    }
}
