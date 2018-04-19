package main.java.com.excilys.cdb.daos;

import java.sql.SQLException;

import main.java.com.excilys.cdb.utils.Page;

public interface DAO<T> {
	
	/**
	 * Find all the objects for the corresponding page
	 * 
	 * @param currentPage	The page to display
	 * @return				A Page object with the informations
	 * @throws SQLException	If there is a problem with the SQL request
	 */
	Page<T> findAll(int currentPage) throws SQLException;
	
	/**
	 * Find an object with his identifier
	 * 
	 * @param id			The identifier
	 * @return				The object searched
	 * @throws SQLException	If there is a problem with the SQL request
	 */
	T findById(int id) throws SQLException;
	
	/**
	 * Add an object to the database
	 * 
	 * @param t				The object to add
	 * @return				true if the object id added to the database, false if not
	 * @throws SQLException	If there is a problem with the SQL request
	 */
	boolean add(T t) throws SQLException;
	
	/**
	 * Delete an object to the database
	 * 
	 * @param t				The object to delete
	 * @return				true if the object id deleted to the database, false if not
	 * @throws SQLException	If there is a problem with the SQL request
	 */
	boolean delete(T t) throws SQLException;

	/**
	 * Update an existing object to the database
	 * 
	 * @param t				The object to update
	 * @return				true if the object id updated, false if not
	 * @throws SQLException	If there is a problem with the SQL request
	 */
	boolean update(T t) throws SQLException;
}
