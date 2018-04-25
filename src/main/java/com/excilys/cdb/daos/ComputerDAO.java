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
 * ComputerDAO make the link between the database and the model.
 * @author emmanuelh
 */
public class ComputerDAO implements DAO<Computer> {

    /**
     * The connection to the database.
     */
    private Connection connection;

    /**
     * The singleton's instance of ComputerDAO.
     */
    private static ComputerDAO computerDAO;

    private final String FIND_ALL_COMPUTERS = "SELECT computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company ON computer.company_id=company.id LIMIT ?,?";
    private final String FIND_COMPUTER_BY_ID = "SELECT computer.id,computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company ON computer.company_id=company.id  WHERE computer.id=?";
    private final String ADD_COMPUTER = "INSERT INTO computer (name, introduced, discontinued, company_id)"
            + "VALUES (?, ?, ?, ?)";
    private final String DELETE_COMPUTER = "DELETE FROM computer WHERE id = ?";
    private final String UPDATE_COMPUTER = "UPDATE computer SET name = ?, introduced = ?, "
            + "discontinued = ?, company_id= ? WHERE computer.id = ?";
    private final String COUNT_COMPUTERS = "SELECT COUNT(id) FROM computer";

    /**
     * The constructor with a Connection.
     * @param connection    The connection
     */
    private ComputerDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Get the singleton's instance of the ComputerDAO.
     * @param connection    The connection
     * @return              The instance of ComputerDAO
     */
    public static ComputerDAO getInstance(Connection connection) {
        if (null == computerDAO) {
            computerDAO = new ComputerDAO(connection);
        }
        return computerDAO;
    }

    @Override
    public Page<Computer> findAll(int currentPage, int maxResults) throws SQLException {
        if (currentPage < 1 || maxResults < 1) {
            return null;
        }

        List<Computer> computers = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement(FIND_ALL_COMPUTERS);
        statement.setInt(1, (currentPage - 1) * maxResults);
        statement.setInt(2, maxResults);
        ResultSet rs = statement.executeQuery();
        Company company;
        while (rs.next()) {
            company = new Company(rs.getLong("company.id"), rs.getString("company.name"));
            computers.add(new Computer.Builder(rs.getString("computer.name")).id(rs.getInt("computer.id"))
                    .introduced(DateConvertor.timeStampToLocalDate(rs.getTimestamp("computer.introduced")))
                    .discontinued(DateConvertor.timeStampToLocalDate(rs.getTimestamp("computer.discontinued")))
                    .manufacturer(company).build());
        }
        rs.close();
        statement.close();

        Page<Computer> page = new Page<>();
        page.setResultsPerPage(maxResults);

        // Count max pages
        statement = connection.prepareStatement(COUNT_COMPUTERS);
        rs = statement.executeQuery();
        if (rs.next()) {
            page.setMaxPage(rs.getInt(1) / page.getResultsPerPage());
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
        // If a computer is found, build it...
        if (rSet.next()) {
            Company company = new Company(rSet.getLong("company.id"), rSet.getString("company.name"));
            computer = new Computer.Builder(rSet.getString("computer.name")).id(rSet.getInt("computer.id"))
                    .introduced(DateConvertor.timeStampToLocalDate(rSet.getTimestamp("computer.introduced")))
                    .discontinued(DateConvertor.timeStampToLocalDate(rSet.getTimestamp("computer.discontinued")))
                    .manufacturer(company)
                    .build();
        }
        return computer;
    }

    @Override
    public long add(Computer computer) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(ADD_COMPUTER, Statement.RETURN_GENERATED_KEYS);

        Timestamp introducedSQL = null;
        Timestamp discontinuedSQL = null;

        // Check if the dates are null or not
        if (null != computer.getIntroduced()) {
            introducedSQL = DateConvertor.localDateToTimeStamp(computer.getIntroduced());
        }
        if (null != computer.getDiscontinued()) {
            discontinuedSQL = DateConvertor.localDateToTimeStamp(computer.getDiscontinued());
        }

        // Give the statement parameters
        statement.setString(1, computer.getName());
        statement.setTimestamp(2, introducedSQL);
        statement.setTimestamp(3, discontinuedSQL);
        if (null == computer.getManufacturer()) {
            statement.setObject(4, null);
        } else {
            statement.setLong(4, computer.getManufacturer().getId());
        }

        // Execute the add request
        statement.executeUpdate();

        // Retrieve the id of the created object
        ResultSet rSet = statement.getGeneratedKeys();
        if (rSet.next()) {
            return rSet.getInt(1);
        }
        return 0;
    }

    @Override
    public boolean delete(long id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_COMPUTER);
        statement.setLong(1, id);
        int result = statement.executeUpdate();
        if (result == 0) {
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
        if (null != computer.getIntroduced()) {
            introducedSQL = DateConvertor.localDateToTimeStamp(computer.getIntroduced());
        }
        if (null != computer.getDiscontinued()) {
            discontinuedSQL = DateConvertor.localDateToTimeStamp(computer.getDiscontinued());
        }

        // Give the statement parameters
        statement.setString(1, computer.getName());
        statement.setTimestamp(2, introducedSQL);
        statement.setTimestamp(3, discontinuedSQL);
        if (null == computer.getManufacturer()) {
            statement.setObject(4, null);
        } else {
            statement.setLong(4, computer.getManufacturer().getId());
        }
        statement.setLong(5, computer.getId());

        int result = statement.executeUpdate();
        if (result == 0) {
            return null;
        }
        return computer;
    }

    /**
     * Count the number of computers in the database.
     * @return              The number of computers
     * @throws SQLException If there is a problem with the SQL request
     */
    public int count() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(COUNT_COMPUTERS);
        ResultSet rSet = statement.executeQuery();
        if (rSet.next()) {
            return rSet.getInt(1);
        }
        return -1;
    }
}
