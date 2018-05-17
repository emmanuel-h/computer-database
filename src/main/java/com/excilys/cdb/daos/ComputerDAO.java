package com.excilys.cdb.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

import com.excilys.cdb.model.Computer;
import com.excilys.cdb.utils.DateConvertor;
import com.excilys.cdb.utils.Page;
import com.excilys.cdb.utils.ResultSetConvertor;

/**
 * ComputerDAO make the link between the database and the model.
 * @author emmanuelh
 */
public class ComputerDAO implements DAO<Computer> {

    /**
     * The singleton's instance of ComputerDAO.
     */
    private static ComputerDAO computerDAO;

    private final String FIND_ALL_COMPUTERS = "SELECT computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company ON computer.company_id=company.id LIMIT ?,?";
    private final String FIND_COMPUTER_BY_ID = "SELECT computer.id,computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company ON computer.company_id=company.id  WHERE computer.id=?";
    private final String ADD_COMPUTER = "INSERT INTO computer (name, introduced, discontinued, company_id, id)"
            + "VALUES (?, ?, ?, ?, ?)";
    private final String DELETE_COMPUTER = "DELETE FROM computer WHERE id = ?";
    private final String DELETE_MULTIPLE_COMPUTER = "DELETE FROM computer WHERE id in %s";
    private final String UPDATE_COMPUTER = "UPDATE computer SET name = ?, introduced = ?, "
            + "discontinued = ?, company_id= ? WHERE computer.id = ?";
    private final String COUNT_COMPUTERS = "SELECT COUNT(id) FROM computer";
    private final String SEARCH_COMPUTERS = "SELECT company.id, company.name, computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company ON computer.company_id=company.id WHERE computer.name LIKE ? LIMIT ?,?";
    private final String COUNT_SEARCHED_COMPUTERS = "SELECT COUNT(id) FROM computer WHERE computer.name LIKE ?";

    /**
     * The constructor with a Connection.
     */
    private ComputerDAO() {
    }

    /**
     * Get the singleton's instance of the ComputerDAO.
     * @return              The instance of ComputerDAO
     */
    public static ComputerDAO getInstance() {
        if (null == computerDAO) {
            computerDAO = new ComputerDAO();
        }
        return computerDAO;
    }

    @Override
    public Page<Computer> findAllWithPaging(int currentPage, int maxResults) throws SQLException {
        if (currentPage < 1 || maxResults < 1) {
            return null;
        }
        try (Connection con = DAOFactory.getConnection();
                PreparedStatement statement = con.prepareStatement(FIND_ALL_COMPUTERS)) {
            statement.setInt(1, (currentPage - 1) * maxResults);
            statement.setInt(2, maxResults);
            try (ResultSet rs = statement.executeQuery()) {
                int totalComputers = count();
                return ResultSetConvertor.resultSetToComputerPage(rs, maxResults, totalComputers, currentPage);
            }
        }
    }

    @Override
    public Optional<Computer> findById(long id) throws SQLException {
        Optional<Computer> computer = Optional.empty();
        try (Connection connection = DAOFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_COMPUTER_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet rSet = statement.executeQuery()) {
                if (rSet.next()) {
                    computer = ResultSetConvertor.resultSetToOptionalComputer(rSet);
                }
            }
        }
        return computer;
    }

    @Override
    public long add(Computer computer) throws SQLException {
        try (Connection connection = DAOFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(ADD_COMPUTER, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp introducedSQL = null;
            Timestamp discontinuedSQL = null;

            if (null != computer.getIntroduced()) {
                introducedSQL = DateConvertor.localDateToTimeStamp(computer.getIntroduced());
            }
            if (null != computer.getDiscontinued()) {
                discontinuedSQL = DateConvertor.localDateToTimeStamp(computer.getDiscontinued());
            }

            statement.setString(1, computer.getName());
            statement.setTimestamp(2, introducedSQL);
            statement.setTimestamp(3, discontinuedSQL);
            if (null == computer.getManufacturer()) {
                statement.setObject(4, null);
            } else {
                statement.setLong(4, computer.getManufacturer().getId());
            }
            statement.setLong(5, computer.getId());

            statement.executeUpdate();

            try (ResultSet rSet = statement.getGeneratedKeys()) {
                if (rSet.next()) {
                    return rSet.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public boolean delete(long id) throws SQLException {
        try (Connection connection = DAOFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE_COMPUTER)) {
            statement.setLong(1, id);
            int result = statement.executeUpdate();
            if (result == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Computer update(Computer computer) throws SQLException {
        try (Connection connection = DAOFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE_COMPUTER)) {

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
        }
        return computer;
    }

    /**
     * Count the number of computers in the database.
     * @return              The number of computers
     * @throws SQLException If there is a problem with the SQL request
     */
    public int count() throws SQLException {
        try (Connection connection = DAOFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(COUNT_COMPUTERS);
                ResultSet rSet = statement.executeQuery()) {
            if (rSet.next()) {
                return rSet.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Delete a list of computers.
     * @param toDelete      The list to delete
     * @return              true if the computers have been deleted, false if not
     * @throws SQLException If there is a problem with the SQL request
     */
    public boolean deleteMultiple(String toDelete) throws SQLException {
        String query = String.format(DELETE_MULTIPLE_COMPUTER, toDelete);
        try (Connection connection = DAOFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            int result = statement.executeUpdate();
            if (result == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Search a computer or a list of computer with a name.
     * @param search          The name to search
     * @param currentPage   The page to display
     * @param maxResults    The number of results per page
     * @return              The list found
     * @throws SQLException If there is a problem with the SQL request
     */
    public Page<Computer> searchComputer(String search, int currentPage, int maxResults) throws SQLException {
        try (Connection connection = DAOFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(SEARCH_COMPUTERS)) {
            statement.setString(1, '%' + search + '%');
            statement.setInt(2, (currentPage - 1) * maxResults);
            statement.setInt(3, maxResults);
            try (ResultSet rs = statement.executeQuery()) {
                int totalComputers = countSearchedComputers(search);
                return ResultSetConvertor.resultSetToComputerPage(rs, maxResults, totalComputers, currentPage);
            }
        }
    }

    /**
     * Count the number of computers corresponding to the search.
     * @param search        The researched String
     * @return              The number of computers
     * @throws SQLException If there is a problem with the SQL request
     */
    public int countSearchedComputers(String search) throws SQLException {
        try (Connection connection = DAOFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(COUNT_SEARCHED_COMPUTERS)) {
            statement.setString(1, '%' + search + '%');
            try (ResultSet rSet = statement.executeQuery()) {
                if (rSet.next()) {
                    return rSet.getInt(1);
                }
            }
        }
        return -1;
    }
}
