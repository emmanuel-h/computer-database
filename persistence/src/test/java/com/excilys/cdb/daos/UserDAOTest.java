package com.excilys.cdb.daos;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.excilys.cdb.configpersistence.SpringConfigDBTest;
import com.excilys.cdb.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigDBTest.class)
public class UserDAOTest {
	
	@Autowired
	private UserDAO userDAO;
	
	@Test
	public void findByUsername() {
		User user = userDAO.findUserByUsername("admin").get();
		assertTrue(user.getUsername().equals("admin"));
		assertTrue(BCrypt.checkpw("admin", user.getPassword()));
		assertTrue(user.isEnabled());
		assertTrue(user.getRoles().get(0).getRole().equals("ADMIN"));
	}

}
