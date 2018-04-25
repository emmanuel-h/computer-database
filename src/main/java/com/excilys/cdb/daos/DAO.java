package com.excilys.cdb.daos;

import java.sql.SQLException;

import com.excilys.cdb.utils.Page;

public interface DAO<T> {

    /**
     * Find all the objects for the corresponding page.
     * @param currentPage   The page to display
     * @param maxResults    The number of result per page
     * @return              A Page object with the informations
     * @throws SQLException If there is a problem with the SQL request
     */
    Page<T> findAll(int currentPage, int maxResults) throws SQLException;

    /**
     * Find an object with his identifier.
     * @param id            The identifier
     * @return              The object searched
     * @throws SQLException If there is a problem with the SQL request
     */
    T findById(long id) throws SQLException;

    /**
     * Add an object to the database.
     * @param t             The object to add
     * @return true         If the object id added to the database, false if not
     * @throws SQLException If there is a problem with the SQL request
     */
    long add(T t) throws SQLException;

    /**
     * Delete an object to the database.
     * @param id            The identifier of the object to delete
     * @return true         If the object id deleted to the database, false if not
     * @throws SQLException If there is a problem with the SQL request
     */
    boolean delete(long id) throws SQLException;

    /**
     * Update an existing object to the database.
     * @param t             The object to update
     * @return true         If the object id updated, false if not
     * @throws SQLException If there is a problem with the SQL request
     */
    T update(T t) throws SQLException;
}
