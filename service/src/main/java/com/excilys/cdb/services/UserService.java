package com.excilys.cdb.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
	    User user = userDao.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	    Set<GrantedAuthority> grantedAuthorities = user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getRole())).collect(Collectors.toSet());  
	    return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities); 
	  }
}
