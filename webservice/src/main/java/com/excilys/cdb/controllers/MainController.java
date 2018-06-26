package com.excilys.cdb.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.excilys.cdb.model.User;
import com.excilys.cdb.services.UserService;

@RestController
//@CrossOrigin(origins = "*", allowCredentials="true", allowedHeaders= {"Content-Type"})
public class MainController {   
    
    private static final String YOU_LOGOUT_SUCCESSFULL = "You logout successfull";
    private static final String LOGIN_USERNAME_PASSWORD_ERROR = "Login username / password error";
    private static final String YOU_NEED_TO_LOGIN_AT_LOGIN_WITH_USERNAME_AND_PASSWORD = "You need to login at /login with username and password";
    private static final String MESSAGE = "Message";
    private static final String USER_CREATED = "User created !";
    private UserService service;
    
    public MainController(UserService userService) {
        this.service = userService;
    }
    
    @GetMapping(path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)  
    public @ResponseBody HashMap<String, String> showLogin() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(MESSAGE, YOU_NEED_TO_LOGIN_AT_LOGIN_WITH_USERNAME_AND_PASSWORD);
        return hashMap;
    }
    
    @PostMapping(path="/loginError")
    public ResponseEntity<String> showErrors(){
     return ResponseEntity.badRequest().body(LOGIN_USERNAME_PASSWORD_ERROR);
    }

    @GetMapping(path="/login?logout")
    public @ResponseBody HashMap<String, String> logout(){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(MESSAGE, YOU_LOGOUT_SUCCESSFULL);
        return hashMap;
    }
    @CrossOrigin(origins = "*", allowCredentials="true", allowedHeaders= {"Content-Type"})
    @GetMapping(path="/user/roles")
    public Set<String> getRoles(){
        if(SecurityContextHolder.getContext().getAuthentication().getAuthorities() == null) {
            return Collections.emptySet();
        }
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(a -> a.toString()).collect(Collectors.toSet());
    }
    
    @CrossOrigin(origins = "*", allowCredentials="true", allowedHeaders= {"Content-Type"})
    @PostMapping(path="/user")
    public ResponseEntity<String> inscription(@RequestBody @Valid final User user){    
       this.service.addUser(user);
       return ResponseEntity.ok(USER_CREATED);
   }
}
