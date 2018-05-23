package com.excilys.cdb.daos;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.daos.mapper.ComputerRowMapper;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.utils.DateConvertor;
import com.excilys.cdb.utils.Page;

/**
 * ComputerDAO make the link between the database and the model.
 * @author emmanuelh
 */
@Repository
public class ComputerDAO implements DAO<Computer> {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate template;

    private final String FIND_ALL_COMPUTERS = "SELECT computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company ON computer.company_id=company.id LIMIT ?,?";
    private final String FIND_COMPUTER_BY_ID = "SELECT computer.id,computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company ON computer.company_id=company.id  WHERE computer.id=?";
    private final String DELETE_COMPUTER = "DELETE FROM computer WHERE id = ?";
    private final String DELETE_MULTIPLE_COMPUTER = "DELETE FROM computer WHERE id in %s";
    private final String UPDATE_COMPUTER = "UPDATE computer SET name = ?, introduced = ?, "
            + "discontinued = ?, company_id= ? WHERE computer.id = ?";
    private final String COUNT_COMPUTERS = "SELECT COUNT(id) FROM computer";
    private final String SEARCH_COMPUTERS = "SELECT company.id, company.name, computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company ON computer.company_id=company.id WHERE computer.name LIKE ? LIMIT ?,?";
    private final String COUNT_SEARCHED_COMPUTERS = "SELECT COUNT(id) FROM computer WHERE computer.name LIKE ?";

    /**
     * Private constructor to ensure uniqueness.
     */
    private ComputerDAO() {
    }

    @PostConstruct
    private void initializeConnection() {
        template = new JdbcTemplate(dataSource);
    }

    @Override
    public Page<Computer> findAllWithPaging(int currentPage, int maxResults) {
        if (currentPage < 1 || maxResults < 1) {
            return null;
        }

        Page<Computer> page = new Page<>();
        List<Computer> computers = new ArrayList<>();

        try {
            computers = template.query(FIND_ALL_COMPUTERS,
                    new Object[] {(currentPage - 1) * maxResults, maxResults},
                    new ComputerRowMapper());
        } catch (EmptyResultDataAccessException e) {
            page.setMaxPage(1);
            page.setCurrentPage(currentPage);
            page.setResultsPerPage(maxResults);
            page.setResults(computers);
            return page;
        }
        int total = template.queryForObject(COUNT_COMPUTERS, Integer.class);

        double maxPage = total / maxResults;
        page.setMaxPage((int) Math.ceil(maxPage));
        page.setCurrentPage(currentPage);
        page.setResultsPerPage(maxResults);
        page.setResults(computers);
        return page;
    }

    @Override
    public Optional<Computer> findById(long id) {
        Computer computer;
        try {
            computer = template.queryForObject(FIND_COMPUTER_BY_ID, new Object[] {id}, new ComputerRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        return Optional.of(computer);
    }

    @Override
    public long add(Computer computer) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource);
        jdbcInsert.withTableName("computer").usingGeneratedKeyColumns("id");

        Timestamp introducedSQL = null;
        Timestamp discontinuedSQL = null;
        if (null != computer.getIntroduced()) {
            introducedSQL = DateConvertor.localDateToTimeStamp(computer.getIntroduced());
        }
        if (null != computer.getDiscontinued()) {
            discontinuedSQL = DateConvertor.localDateToTimeStamp(computer.getDiscontinued());
        }

        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("name", computer.getName())
                .addValue("introduced", introducedSQL)
                .addValue("discontinued", discontinuedSQL)
                .addValue("id", computer.getId());

        if (null == computer.getManufacturer()) {
            ((MapSqlParameterSource) parameterSource).addValue("company_id", null);
        } else {
            ((MapSqlParameterSource) parameterSource).addValue("company_id", computer.getManufacturer().getId());
        }

        return jdbcInsert.executeAndReturnKey(parameterSource).longValue();

    }

    @Override
    public boolean delete(long id) {
        int result = template.update(DELETE_COMPUTER, id);
        return !(result == 0);
    }

    @Override
    public Computer update(Computer computer) {
        Timestamp introducedSQL = null;
        Timestamp discontinuedSQL = null;
        if (null != computer.getIntroduced()) {
            introducedSQL = DateConvertor.localDateToTimeStamp(computer.getIntroduced());
        }
        if (null != computer.getDiscontinued()) {
            discontinuedSQL = DateConvertor.localDateToTimeStamp(computer.getDiscontinued());
        }

        Object[] args = new Object[5];
        args[0] = computer.getName();
        args[1] = introducedSQL;
        args[2] = discontinuedSQL;

        if (null == computer.getManufacturer()) {
            args[3] = null;
        } else {
            args[3] = computer.getManufacturer().getId();
        }
        args[4] = computer.getId();

        int result = template.update(UPDATE_COMPUTER, args);

        if (result == 0) {
            return null;
        } else {
            return computer;
        }
    }

    /**
     * Count the number of computers in the database.
     * @return              The number of computers
     * @throws SQLException If there is a problem with the SQL request
     */
    public int count() {
        return template.queryForObject(COUNT_COMPUTERS, Integer.class);
    }

    /**
     * Delete a list of computers.
     * @param toDelete      The list to delete
     * @return              true if the computers have been deleted, false if not
     * @throws SQLException If there is a problem with the SQL request
     */
    public boolean deleteMultiple(String toDelete) {
        int result = template.update(String.format(DELETE_MULTIPLE_COMPUTER, toDelete));
        return !(result == 0);
    }

    /**
     * Search a computer or a list of computer with a name.
     * @param search          The name to search
     * @param currentPage   The page to display
     * @param maxResults    The number of results per page
     * @return              The list found
     * @throws SQLException If there is a problem with the SQL request
     */
    public Page<Computer> searchComputer(String search, int currentPage, int maxResults) {
        if (currentPage < 1 || maxResults < 1) {
            return null;
        }

        Page<Computer> page = new Page<>();
        List<Computer> computers = new ArrayList<>();

        try {
            computers = template.query(SEARCH_COMPUTERS,
                    new Object[] {'%' + search + '%', (currentPage - 1) * maxResults, maxResults},
                    new ComputerRowMapper());
        } catch (EmptyResultDataAccessException e) {
            page.setMaxPage(1);
            page.setCurrentPage(currentPage);
            page.setResultsPerPage(maxResults);
            page.setResults(computers);
            return page;
        }
        int total = countSearchedComputers(search);

        double maxPage = total / maxResults;
        page.setMaxPage((int) Math.ceil(maxPage));
        page.setCurrentPage(currentPage);
        page.setResultsPerPage(maxResults);
        page.setResults(computers);
        return page;
    }

    /**
     * Count the number of computers corresponding to the search.
     * @param search        The researched String
     * @return              The number of computers
     * @throws SQLException If there is a problem with the SQL request
     */
    public int countSearchedComputers(String search) {
        return template.queryForObject(COUNT_SEARCHED_COMPUTERS, new Object[] {'%' + search + '%'}, Integer.class);
    }
}
