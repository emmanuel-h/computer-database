package main.java.com.excilys.cdb.daos;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {
	
	List<T> findAll() throws SQLException;
	T findById(int id) throws SQLException;
	boolean add(T t) throws SQLException;
	boolean delete(T t) throws SQLException;
	boolean update(T t) throws SQLException;
}
