package com.excilys.cdb.daos;

import java.sql.SQLException;
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

import com.excilys.cdb.daos.mapper.CompanyRowMapper;
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

    private JdbcTemplate template;

    private final String FIND_ALL_COMPANIES = "SELECT id, name FROM company";
    private final String FIND_ALL_COMPANIES_WITH_PAGING = "SELECT id, name FROM company LIMIT ?,?";
    private final String FIND_COMPANY_BY_ID = "SELECT id, name FROM company WHERE id=?";
    private final String DELETE_COMPANY = "DELETE FROM company WHERE id = ?";
    private final String UPDATE_COMPANY = "UPDATE company SET company.name = ? WHERE company.id = ?";
    private final String COUNT_COMPANIES = "SELECT COUNT(id) FROM company";
    private final String DELETE_COMPUTER_FROM_MANUFACTURER = "DELETE FROM computer WHERE computer.company_id = ?";

    /**
     * Private constructor to ensure uniqueness.
     */
    private CompanyDAO() {
    }

    @PostConstruct
    private void initializeConnection() {
        template = new JdbcTemplate(dataSource);
    }

    @Override
    public Page<Company> findAllWithPaging(int currentPage, int maxResults) {
        if (currentPage < 1 || maxResults < 1) {
            return null;
        }
        Page<Company> page = new Page<>();
        List<Company> companies = new ArrayList<>();
        try {
            companies = template.query(FIND_ALL_COMPANIES_WITH_PAGING,
                    new Object[] {(currentPage - 1) * maxResults, maxResults},
                    new CompanyRowMapper());
        } catch (EmptyResultDataAccessException e) {
            page.setMaxPage(1);
            page.setCurrentPage(currentPage);
            page.setResultsPerPage(maxResults);
            page.setResults(companies);
            return page;
        }
        int total = template.queryForObject(COUNT_COMPANIES, Integer.class);

        double maxPage = total / page.getResultsPerPage();
        page.setMaxPage((int) Math.ceil(maxPage));
        page.setCurrentPage(currentPage);
        page.setResultsPerPage(maxResults);
        page.setResults(companies);
        return page;
    }

    @Override
    public Optional<Company> findById(long id) {

        Company company;
        try {
            company = template.queryForObject(FIND_COMPANY_BY_ID, new Object[] {id}, new CompanyRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        return Optional.of(company);
    }

    @Override
    public long add(Company company) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource);
        jdbcInsert.withTableName("company").usingGeneratedKeyColumns("id");
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("name", company.getName())
                .addValue("id", company.getId());
        return jdbcInsert.executeAndReturnKey(parameterSource).longValue();
    }

    @Override
    public boolean delete(long id) {
        template.update(DELETE_COMPUTER_FROM_MANUFACTURER, id);
        return !(template.update(DELETE_COMPANY, id) == 0);
    }

    @Override
    public Company update(Company company) {
        int result = template.update(UPDATE_COMPANY, new Object[] {company.getName(), company.getId()});
        if (result == 0) {
            return null;
        } else {
            return company;
        }
    }

    /**
     * Find all companies.
     * @return The list of companies, or an empty List if there is no one
     * @throws SQLException If there is a problem with the SQL request
     */
    public List<Company> findAll() {
        return template.query(FIND_ALL_COMPANIES, new CompanyRowMapper());
    }
}
