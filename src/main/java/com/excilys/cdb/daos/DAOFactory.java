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

public class DAOFactory {
	
	private static Connection connection;
	private static DAOFactory daoFactory;
	private final static Logger LOGGER = LoggerFactory.getLogger(DAOFactory.class);
	
	public static enum DaoTypes {COMPUTER, COMPANY};
	
	private DAOFactory() throws FactoryException {
		try {
			Properties properties = new Properties();
			String propFileName="main/ressources/config-db.properties";
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			properties.load(inputStream);
			
			String databaseURL = properties.getProperty("database-url");
			String user = properties.getProperty("database-user");
			String password = properties.getProperty("database-password");
			
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

	public static DAO<?> getDAO(DaoTypes daoType) throws FactoryException {
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
	
	@Override
	protected void finalize() throws Throwable {
		connection.close();
	}
}
