package com.excilys.cdb.daos.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.excilys.cdb.model.Company;

public class CompanyRowMapper implements RowMapper<Company> {

    @Override
    public Company mapRow(ResultSet resultSet, int line) throws SQLException {
        CompanyResultSetExtractor extractor = new CompanyResultSetExtractor();
        return extractor.extractData(resultSet);
    }

}
