package com.excilys.cdb.daos.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.excilys.cdb.model.Computer;

public class ComputerRowMapper implements RowMapper<Computer> {

    @Override
    public Computer mapRow(ResultSet resultSet, int line) throws SQLException {
        ComputerResultSetExtractor extractor = new ComputerResultSetExtractor();
        return extractor.extractData(resultSet);
    }

}
