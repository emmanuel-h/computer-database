package com.excilys.cdb.daos.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.utils.DateConvertor;

public class ComputerResultSetExtractor  implements ResultSetExtractor<Computer> {

    @Override
    public Computer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Company company = new Company(resultSet.getLong("company.id"), resultSet.getString("company.name"));
        return new Computer.Builder(resultSet.getString("computer.name")).id(resultSet.getInt("computer.id"))
                .introduced(DateConvertor.timeStampToLocalDate(resultSet.getTimestamp("computer.introduced")))
                .discontinued(DateConvertor.timeStampToLocalDate(resultSet.getTimestamp("computer.discontinued")))
                .manufacturer(company)
                .build();
    }

}
