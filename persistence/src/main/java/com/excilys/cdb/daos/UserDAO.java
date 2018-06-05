package com.excilys.cdb.daos;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.User;

@Repository
public class UserDAO {

	private SessionFactory sessionFactory;

	private UserDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public User findUserByUsername(String username) {
		User user;
		try (Session session = sessionFactory.getCurrentSession()) {
			session.beginTransaction();
			user = session.get(User.class, username);
		}
		return user;
	}
}
