package main.java.com.excilys.cdb.daos;

import java.sql.SQLException;
import java.util.List;

import main.java.com.excilys.cdb.utils.Page;

public interface DAO<T> {
	
	Page<T> findAll(int currentPage) throws SQLException;
	T findById(int id) throws SQLException;
	boolean add(T t) throws SQLException;
	boolean delete(T t) throws SQLException;
	boolean update(T t) throws SQLException;
}
