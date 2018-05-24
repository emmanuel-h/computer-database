package com.excilys.cdb.daos.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.excilys.cdb.model.Company;

public class CompanyResultSetExtractor implements ResultSetExtractor<Company> {

    @Override
    public Company extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        return new Company(resultSet.getLong("company.id"), resultSet.getString("company.name"));
    }

}
