package com.excilys.cdb.daos;

import java.util.Optional;

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
    
	public Optional<User> findUserByUsername(String username) {
		Optional<User> user = Optional.empty();
		try (Session session = sessionFactory.getCurrentSession()) {
			session.beginTransaction();
			user = Optional.ofNullable(session.get(User.class, username));
		}
		return user;
	}
	
	public void addUser(User user) {
	    try (Session session = sessionFactory.getCurrentSession()){
	        session.beginTransaction();
	        session.save(user);
	        session.flush();
	    }
	}
}
