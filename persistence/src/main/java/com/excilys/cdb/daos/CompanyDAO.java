package com.excilys.cdb.daos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.Page;

/**
 * ComputerDAO make the link between the database and the model.
 * @author emmanuelh
 */
@Repository
public class CompanyDAO implements DAO<Company> {

    private final String FIND_ALL_COMPANIES = "FROM Company";
    private final String FIND_ALL_COMPANIES_WITH_PAGING = "FROM Company";
    private final String DELETE_COMPANY = "DELETE FROM Company WHERE id = :id";
    private final String UPDATE_COMPANY = "UPDATE Company SET name = :name WHERE id = :id";
    private final String COUNT_COMPANIES = "SELECT COUNT(id) FROM Company";
    private final String DELETE_COMPUTER_FROM_MANUFACTURER = "DELETE FROM Computer WHERE manufacturer = :manufacturer";
    private final String SEARCH_COMPANIES = "FROM Company WHERE name LIKE :search";
    private final String COUNT_SEARCHED_COMPANIES = "SELECT COUNT(id) FROM Company WHERE name LIKE :search";
    private final String COUNT_COMPUTERS_OF_COMPANY = "SELECT COUNT(id) FROM Computer WHERE manufacturer = :manufacturer";
    private final String INCREMENT_COMPUTERS_OF_COMPANY = "UPDATE Company SET number_of_computers = number_of_computers + 1 WHERE id = :id";
    private final String DECREMENT_COMPUTERS_OF_COMPANY = "UPDATE Company SET number_of_computers = number_of_computers - 1 WHERE id = :id";
    
    private SessionFactory sessionFactory;

    /**
     * Private constructor to ensure uniqueness.
     * @param sessionFactory    The session factory
     */
    private CompanyDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Page<Company> findAllWithPaging(int currentPage, int maxResults) {

        if (currentPage < 1 || maxResults < 1) {
            return null;
        }

        Page<Company> page = new Page<>();
        List<Company> computers = new ArrayList<>();
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            TypedQuery<Company> query = session.createQuery(FIND_ALL_COMPANIES_WITH_PAGING, Company.class)
                    .setFirstResult((currentPage - 1) * maxResults)
                    .setMaxResults(maxResults);
            computers = query.getResultList();
        }
        int total = 1;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(COUNT_COMPANIES);
            total = (int) (long) query.getResultList().get(0);
        }
        page.setMaxPage((int) Math.ceil((double) total / (double) maxResults));
        page.setCurrentPage(currentPage);
        page.setResultsPerPage(maxResults);
        page.setResults(computers);
        return page;
    }

    @Override
    public Optional<Company> findById(long id) {
        Company company = null;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            company = session.get(Company.class, id);
        }
        return Optional.ofNullable(company);
    }

    @Override
    public long add(Company company) {
        long id = -1;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            id = (long) session.save(company);
        }
        return id;
    }

    @Override
    public boolean delete(long id) {
        int result = 0;
        Transaction transaction = null;
        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();
            Company manufacturer = new Company(id);
            Query query = session.createQuery(DELETE_COMPUTER_FROM_MANUFACTURER);
            query.setParameter("manufacturer", manufacturer);
            query.executeUpdate();
            query = session.createQuery(DELETE_COMPANY);
            query.setParameter("id", id);
            result = query.executeUpdate();
            transaction.commit();
        } catch (RuntimeException e) {
            if (null != transaction && !transaction.isActive()) {
                transaction.rollback();
            }
        }
        return !(result == 0);
    }

    @Override
    public Company update(Company company) {
        long result;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(UPDATE_COMPANY);
            query.setParameter("name", company.getName());
            query.setParameter("id", company.getId());
            result = query.executeUpdate();
        }
        if (result == 0) {
            return null;
        } else {
            return company;
        }
    }

    /**
     * Find all companies.
     * @return The list of companies, or an empty List if there is no one
     */
    public List<Company> findAll() {
        List<Company> companies = new ArrayList<>();
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            TypedQuery<Company> query = session.createQuery(FIND_ALL_COMPANIES, Company.class);
            companies = query.getResultList();
        }
        return companies;
    }

    /**
     * Count the number of computers in the database.
     * @return              The number of computers
     */
    public int count() {
        long total = 0;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(COUNT_COMPANIES);
            total = (long) query.getResultList().get(0);
        }
        return (int) total;
    }

    /**
     * Search a company or a list of companies with a name.
     * @param search          The name to search
     * @param currentPage   The page to display
     * @param maxResults    The number of results per page
     * @return              The list found
     */
    public Page<Company> searchCompany(String search, int currentPage, int maxResults) {
        if (currentPage < 1 || maxResults < 1) {
            return null;
        }

        Page<Company> page = new Page<>();
        List<Company> companies = new ArrayList<>();
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            TypedQuery<Company> query = session.createQuery(SEARCH_COMPANIES, Company.class)
                    .setFirstResult((currentPage - 1) * maxResults)
                    .setMaxResults(maxResults);
            query.setParameter("search", '%' + search + '%');
            companies = query.getResultList();
        }

        int total = countSearchedCompanies(search);
        page.setMaxPage((int) Math.ceil((double) total / (double) maxResults));
        page.setCurrentPage(currentPage);
        page.setResultsPerPage(maxResults);
        page.setResults(companies);
        return page;
    }

    /**
     * Count the number of companies corresponding to the search.
     * @param search        The researched String
     * @return              The number of companies
     */
    public int countSearchedCompanies(String search) {
        long total = 0;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(COUNT_SEARCHED_COMPANIES);
            query.setParameter("search", '%' + search + '%');
            total = (long) query.getResultList().get(0);
        }
        return (int) total;
    }
    
    /**
     * Count the number of computers with a certain manufacturer.
     * @param id	The id of the manufacturer
     * @return		The number of searched computers
     */
    public int countComputersOfCompany(long id) {
    	long total = 0;
    	try (Session session = sessionFactory.getCurrentSession()) {
    		session.beginTransaction();
    		Query query = session.createQuery(COUNT_COMPUTERS_OF_COMPANY);
    		query.setParameter("manufacturer", new Company(id));
            total = (long) query.getResultList().get(0);
    	}
        return (int) total;
    }
    
    public boolean incrementComputersOfCompany(long id) {
        long result;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(INCREMENT_COMPUTERS_OF_COMPANY);
            query.setParameter("id", id);
            result = query.executeUpdate();
        }
        return !(result == 0);
    }
    
    public boolean decrementComputersOfCompany(long id) {
        long result;
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(DECREMENT_COMPUTERS_OF_COMPANY);
            query.setParameter("id", id);
            result = query.executeUpdate();
        }
        return !(result == 0);
    }
}
