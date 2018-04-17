package main.java.com.excilys.cdb.daos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import main.java.com.excilys.cdb.exceptions.FactoryException;

public class DAOFactory {
	
	private static Connection connection;
	private static DAOFactory daoFactory;
	
	public static enum DaoTypes {COMPUTER, COMPANY};
	
	private DAOFactory() throws FactoryException {
		try {
			connection = DriverManager.getConnection(
			        "jdbc:mysql://localhost:3306/computer-database-db",
			        "admincdb",
			        "qwerty1234");
		} catch (SQLException e) {
			throw new FactoryException("Error when initiating SQL connection");
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
