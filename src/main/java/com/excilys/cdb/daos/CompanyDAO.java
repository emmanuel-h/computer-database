package com.excilys.cdb.daos;

import java.sql.SQLException;
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
import com.excilys.cdb.utils.Page;

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

    /**
     * Private constructor to ensure uniqueness.
     */
    private CompanyDAO() {
    }

    @Override
    public Page<Company> findAllWithPaging(int currentPage, int maxResults) {

        if (currentPage < 1 || maxResults < 1) {
            return null;
        }

        Page<Company> page = new Page<>();
        List<Company> computers = new ArrayList<>();
        SessionFactory sessionFactory = SessionFactoryManager.INSTANCE.getSessionFactory();
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
        page.setMaxPage(total);
        page.setCurrentPage(currentPage);
        page.setResultsPerPage(maxResults);
        page.setResults(computers);
        return page;
    }

    @Override
    public Optional<Company> findById(long id) {
        Company company = null;
        SessionFactory sessionFactory = SessionFactoryManager.INSTANCE.getSessionFactory();
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            company = session.get(Company.class, id);
        }
        return Optional.ofNullable(company);
    }

    @Override
    public long add(Company company) {
        long id = -1;
        SessionFactory sessionFactory = SessionFactoryManager.INSTANCE.getSessionFactory();
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
        SessionFactory sessionFactory = SessionFactoryManager.INSTANCE.getSessionFactory();
        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();
            Query query = session.createQuery(DELETE_COMPUTER_FROM_MANUFACTURER);
            query.setParameter("id", id);
            query.executeUpdate();
            query = session.createQuery(DELETE_COMPANY);
            query.setParameter("id", id);
            result = query.executeUpdate();
            transaction.commit();
        } catch (RuntimeException e) {
            if (null != transaction) {
                transaction.rollback();
            }
        }
        return !(result == 0);
    }

    @Override
    public Company update(Company company) {

        SessionFactory sessionFactory = SessionFactoryManager.INSTANCE.getSessionFactory();
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery(UPDATE_COMPANY);
            query.setParameter("name", company.getName());
            query.setParameter("id", company.getId());
            query.executeUpdate();
        }
        return company;
    }

    /**
     * Find all companies.
     * @return The list of companies, or an empty List if there is no one
     * @throws SQLException If there is a problem with the SQL request
     */
    public List<Company> findAll() {
        List<Company> companies = new ArrayList<>();
        SessionFactory sessionFactory = SessionFactoryManager.INSTANCE.getSessionFactory();
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            TypedQuery<Company> query = session.createQuery(FIND_ALL_COMPANIES, Company.class);
            companies = query.getResultList();
        }
        return companies;
    }
}
