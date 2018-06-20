package com.excilys.cdb.daos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.Computer;
import com.excilys.cdb.Page;

/**
 * ComputerDAO make the link between the database and the model.
 * @author emmanuelh
 */
@Repository
public class ComputerDAO implements DAO<Computer> {

    private final String FIND_ALL_COMPUTERS = "FROM Computer";
    private final String DELETE_COMPUTER = "DELETE FROM Computer WHERE id = :id";
    private final String DELETE_MULTIPLE_COMPUTER = "DELETE FROM Computer WHERE id in %s";
    private final String UPDATE_COMPUTER = "UPDATE Computer SET name = :name, introduced = :introduced, "
            + "discontinued = :discontinued, manufacturer= :manufacturer WHERE id = :id";
    private final String COUNT_COMPUTERS = "SELECT COUNT(id) FROM Computer";
    private final String SEARCH_COMPUTERS = "FROM Computer WHERE name LIKE :search";
    private final String COUNT_SEARCHED_COMPUTERS = "SELECT COUNT(id) FROM Computer WHERE name LIKE :search";
    private final String FIND_ALL_COMPUTERS_WITH_PAGING_AND_SORTING = "FROM Computer ORDER BY ";
    private final String ASC = " ASC";
    private final String DESC = " DESC";
    
    private SessionFactory sessionFactory;

    /**
     * Private constructor to ensure uniqueness.
     * @param sessionFactory    The session factory
     */
    private ComputerDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Page<Computer> findAllWithPaging(int currentPage, int maxResults) {
        if (currentPage < 1 || maxResults < 1) {
            return null;
        }

        Page<Computer> page = new Page<>();
        List<Computer> computers = new ArrayList<>();
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            TypedQuery<Computer> query = session.createQuery(FIND_ALL_COMPUTERS, Computer.class)
                    .setFirstResult((currentPage - 1) * maxResults)
                    .setMaxResults(maxResults);
            computers = query.getResultList();
        }

        int total = count();
        page.setMaxPage((int) Math.ceil((double) total / (double) maxResults));
        page.setCurrentPage(currentPage);
        page.setResultsPerPage(maxResults);
        page.setResults(computers);
        return page;
    }

    @Override
    public Optional<Computer> findById(long id) {
        Computer computer = null;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            computer = session.get(Computer.class, id);
        }
        return Optional.ofNullable(computer);
    }

    @Override
    public long add(Computer computer) {
        long id = -1;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            id = (long) session.save(computer);
        }
        return id;

    }

    @Override
    public boolean delete(long id) {
        int result = 0;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(DELETE_COMPUTER);
            query.setParameter("id", id);
            result = query.executeUpdate();
        }
        return !(result == 0);
    }

    @Override
    public Computer update(Computer computer) {
        long result = 0;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(UPDATE_COMPUTER);
            query.setParameter("name", computer.getName());
            query.setParameter("introduced", computer.getIntroduced());
            query.setParameter("discontinued", computer.getDiscontinued());
            query.setParameter("manufacturer", computer.getManufacturer());
            query.setParameter("id", computer.getId());
            result = query.executeUpdate();
        }
        if (result == 0) {
            return null;
        } else {
            return computer;
        }
    }

    /**
     * Count the number of computers in the database.
     * @return              The number of computers
     */
    public int count() {
        long total = 0;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(COUNT_COMPUTERS);
            total = (long) query.getResultList().get(0);
        }
        return (int) total;
    }

    /**
     * Delete a list of computers.
     * @param toDelete      The list to delete
     * @return              true if the computers have been deleted, false if not
     */
    public boolean deleteMultiple(String toDelete) {
        int result = 0;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(String.format(DELETE_MULTIPLE_COMPUTER, toDelete));
            result = query.executeUpdate();
        }
        return !(result == 0);
    }

    /**
     * Search a computer or a list of computer with a name.
     * @param search          The name to search
     * @param currentPage   The page to display
     * @param maxResults    The number of results per page
     * @return              The list found
     */
    public Page<Computer> searchComputer(String search, int currentPage, int maxResults) {
        if (currentPage < 1 || maxResults < 1) {
            return null;
        }

        Page<Computer> page = new Page<>();
        List<Computer> computers = new ArrayList<>();
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            TypedQuery<Computer> query = session.createQuery(SEARCH_COMPUTERS, Computer.class)
                    .setFirstResult((currentPage - 1) * maxResults)
                    .setMaxResults(maxResults);
            query.setParameter("search", '%' + search + '%');
            computers = query.getResultList();
        }

        int total = countSearchedComputers(search);
        page.setMaxPage((int) Math.ceil((double) total / (double) maxResults));
        page.setCurrentPage(currentPage);
        page.setResultsPerPage(maxResults);
        page.setResults(computers);
        return page;
    }

    /**
     * Count the number of computers corresponding to the search.
     * @param search        The researched String
     * @return              The number of computers
     */
    public int countSearchedComputers(String search) {
        long total = 0;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(COUNT_SEARCHED_COMPUTERS);
            query.setParameter("search", '%' + search + '%');
            total = (long) query.getResultList().get(0);
        }
        return (int) total;
    }
}
