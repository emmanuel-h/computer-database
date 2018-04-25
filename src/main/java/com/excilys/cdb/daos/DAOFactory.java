package com.excilys.cdb.daos;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.exceptions.FactoryException;

/**
 * The DAOFactory is used to have instances of class implementing the DAO.
 * interface
 * @author emmanuelh
 */
public class DAOFactory {

    /**
     * The database connection.
     */
    private static Connection connection;

    /**
     * The static reference to the DAOFactory.
     */
    private static DAOFactory daoFactory;

    /**
     * The differents DAO types which exist.
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
        try {
            // Retrieve the properties file to initiate the connection
            Properties properties = new Properties();
            String propFileName = "config-db.properties";
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            properties.load(inputStream);

            String databaseURL = properties.getProperty("database-url");
            String user = properties.getProperty("database-user");
            String password = properties.getProperty("database-password");

            // Initiate the connection
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(databaseURL, user, password);

            computerDAO = ComputerDAO.getInstance(connection);
            companyDAO = CompanyDAO.getInstance(connection);
            LOGGER.info("Database connected : " + databaseURL);
        } catch (SQLException e) {
            throw new FactoryException("Error when initiating SQL connection");
        } catch (IOException e) {
            LOGGER.warn("Properties file not found");
        } catch (ClassNotFoundException e) {
            LOGGER.warn(e.toString());
        }
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
     * If the garbage collector delete this object, close the connection.
     */
    @Override
    protected void finalize() throws Throwable {
        connection.close();
    }
}
