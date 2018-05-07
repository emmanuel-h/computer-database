package com.excilys.cdb.daos;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.exceptions.FactoryException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The DAOFactory is used to have instances of class implementing the DAO.
 * interface
 * @author emmanuelh
 */
public class DAOFactory {

    /**
     * The Hikari datasource.
     */
    private static HikariDataSource dataSource;

    /**
     * The static reference to the DAOFactory.
     */
    private static DAOFactory daoFactory;

    /**
     * The different DAO types which exist.
     * @author emmanuelh
     */
    public enum DaoTypes {
        COMPUTER, COMPANY
    };

    /**
     * The computer DAO.
     */
    private static ComputerDAO computerDAO;

    /**
     * The company DAO.
     */
    private static CompanyDAO companyDAO;

    /**
     * A logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DAOFactory.class);

    /**
     * Initiate the connection to the database and created the two singletons computerDAO and companyDAO.
     * @throws FactoryException If the database connection failed
     */
    private DAOFactory() throws FactoryException {
        HikariConfig config = new HikariConfig("/some/path/hikari.properties");
        HikariDataSource ds = new HikariDataSource(config);

        computerDAO = ComputerDAO.getInstance();
        companyDAO = CompanyDAO.getInstance();
        LOGGER.info("Database connected : " + ds.getDataSourceClassName());
    }

    /**
     * Get an instance of the desired DAO.
     * @param daoType The DAO type desired
     * @return An instance of the DAO matching the wanted type
     * @throws FactoryException If the DAO type is null or not corresponding to a
     *             known type
     */
    public static DAO<?> getDAO(DaoTypes daoType) throws FactoryException {
        // Singleton use
        if (null == daoFactory) {
            daoFactory = new DAOFactory();
        }
        if (null == daoType) {
            throw new FactoryException("DAO type is null");
        }
        switch (daoType) {
        case COMPUTER:
            return computerDAO;
        case COMPANY:
            return companyDAO;
        default:
            throw new FactoryException("Bad DAO type");
        }
    }

    /**
     * Return the connection from the Hikari datasource.
     * @return              The connection
     * @throws SQLException If there is a problem
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * If the garbage collector delete this object, close the connection.
     */
    @Override
    protected void finalize() throws Throwable {
        connection.close();
    }
}
