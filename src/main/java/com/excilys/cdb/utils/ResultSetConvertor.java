package com.excilys.cdb.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

public class ResultSetConvertor {

    /**
     * Convert a ResultSet into a list of computers.
     * @param resultSet     The desired resultset to convert
     * @return              The list of computers
     * @throws SQLException If there is a problem with the SQL request
     */
    public static List<Computer> resultSetToComputerList(ResultSet resultSet) throws SQLException {
        List<Computer> computers = new ArrayList<>();
        Company company;
        while (resultSet.next()) {
            company = new Company(resultSet.getLong("company.id"), resultSet.getString("company.name"));
            computers.add(new Computer.Builder(resultSet.getString("computer.name")).id(resultSet.getInt("computer.id"))
                    .introduced(DateConvertor.timeStampToLocalDate(resultSet.getTimestamp("computer.introduced")))
                    .discontinued(DateConvertor.timeStampToLocalDate(resultSet.getTimestamp("computer.discontinued")))
                    .manufacturer(company).build());
        }
        return computers;
    }

    /**
     * Convert a ResultSet to a page of computers.
     * @param resultSet         The resultSet to convert
     * @param maxResults        The results per page
     * @param totalComputers    The total count of computers
     * @param currentPage       The currentPage displayed
     * @return                  The page created
     * @throws SQLException     If there is a problem with the SQL request
     */
    public static Page<Computer> resultSetToComputerPage(ResultSet resultSet, int maxResults, int totalComputers, int currentPage) throws SQLException {
        List<Computer> computers = new ArrayList<>();
        Company company;
        while (resultSet.next()) {
            company = new Company(resultSet.getLong("company.id"), resultSet.getString("company.name"));
            computers.add(new Computer.Builder(resultSet.getString("computer.name")).id(resultSet.getInt("computer.id"))
                    .introduced(DateConvertor.timeStampToLocalDate(resultSet.getTimestamp("computer.introduced")))
                    .discontinued(DateConvertor.timeStampToLocalDate(resultSet.getTimestamp("computer.discontinued")))
                    .manufacturer(company).build());
        }


        Page<Computer> page = new Page<>();
        page.setResultsPerPage(maxResults);
        double maxPage = (double) totalComputers / page.getResultsPerPage();
        page.setMaxPage((int) Math.ceil(maxPage));

        page.setCurrentPage(currentPage);
        page.setResults(computers);
        return page;
    }

    /**
     * Convert a ResultSet to an Optional of Computer.
     * @param resultSet     The ResultSet
     * @return              The Optional
     * @throws SQLException If there is a problem with the SQL request
     */
    public static Optional<Computer> resultSetToOptionalComputer(ResultSet resultSet) throws SQLException {
        Optional<Computer> computer = Optional.empty();
        if (resultSet.next()) {
            Company company = new Company(resultSet.getLong("company.id"), resultSet.getString("company.name"));
            computer = Optional.of(new Computer.Builder(resultSet.getString("computer.name")).id(resultSet.getInt("computer.id"))
                    .introduced(DateConvertor.timeStampToLocalDate(resultSet.getTimestamp("computer.introduced")))
                    .discontinued(DateConvertor.timeStampToLocalDate(resultSet.getTimestamp("computer.discontinued")))
                    .manufacturer(company)
                    .build());
        }
        return computer;
    }
}
