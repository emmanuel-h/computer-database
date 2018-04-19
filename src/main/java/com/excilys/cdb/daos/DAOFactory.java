package main.java.com.excilys.cdb.daos;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.cdb.exceptions.FactoryException;

/**
 * The DAOFactory is used to have instances of class implementing the DAO interface
 * 
 * @author emmanuelh
 *
 */
public class DAOFactory {
	
	/**
	 * The database connection
	 */
	private static Connection connection;
	
	/**
	 * The static reference to the DAOFactory
	 */
	private static DAOFactory daoFactory;
	
	/**
	 * The differents DAO types which exist
	 * 
	 * @author emmanuelh
	 *
	 */
	public enum DaoTypes {COMPUTER, COMPANY};
	
	/**
	 * A logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DAOFactory.class);
	
	
	private DAOFactory() throws FactoryException {
		try {
			// Retrieve the properties file to initiate the connection
			Properties properties = new Properties();
			String propFileName="main/ressources/config-db.properties";
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			properties.load(inputStream);
			
			String databaseURL = properties.getProperty("database-url");
			String user = properties.getProperty("database-user");
			String password = properties.getProperty("database-password");
			
			// Initiate the connection
			connection = DriverManager.getConnection(
			        databaseURL,
			        user,
			        password);
		} catch (SQLException e) {
			throw new FactoryException("Error when initiating SQL connection");
		} catch (IOException e) {
			LOGGER.warn("Properties file not found");
		}
	}

	/**
	 * Get an instance of the desired DAO
	 * 
	 * @param daoType			The DAO type desired
	 * @return					An instance of the DAO matching the wanted type
	 * @throws FactoryException	If the DAO type is null or not corresponding to a known type
	 */
	public static DAO<?> getDAO(DaoTypes daoType) throws FactoryException {
		// Singleton use
		if(null == daoFactory) {
			daoFactory = new DAOFactory();
		}
		if (null == daoType) {
			throw new FactoryException("DAO type is null");
		}
		switch(daoType) {
		case COMPUTER:
			return new ComputerDAO(connection);
		case COMPANY:
			return new CompanyDAO(connection);
		default:
			throw new FactoryException("Bad DAO type");
		}
	}
	
	/**
	 * If the garbage collector delete this object, close the connection
	 */
	@Override
	protected void finalize() throws Throwable {
		connection.close();
	}
}
