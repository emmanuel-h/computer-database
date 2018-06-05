package com.excilys.cdb.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.excilys.cdb.daos.UserDAO;
import com.excilys.cdb.model.User;

@Service
public class UserService implements UserDetailsService {

	  @Autowired
	  private UserDAO userDao;
	  
	  private UserService(UserDAO userDAO) {
		  this.userDao = userDAO;
	  }

	  @Override
	  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

	    User user = userDao.findUserByUsername(username);
	    UserBuilder builder = null;
	    if (user != null) {
	      
	      builder = org.springframework.security.core.userdetails.User.withUsername(username);
	      builder.disabled(!user.isEnabled());
	      builder.password(user.getPassword());
	      String[] authorities = user.getRoles()
	          .stream().map(a -> a.getRole()).toArray(String[]::new);

	      builder.authorities(authorities);
	    } else {
	      throw new UsernameNotFoundException("User not found.");
	    }
	    return builder.build();
	  }
}
