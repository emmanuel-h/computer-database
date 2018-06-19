package com.excilys.cdb.controllers;

import java.util.HashMap;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowCredentials="true", allowedHeaders= {"x-auth-token","x-requested-with","x-xsrf-token","X-Requested-With","Content-Type","Access-Control-Allow-Origin"})
public class MainController {
    
    @GetMapping(path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)  
    public @ResponseBody HashMap<String, String> showLogin() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Message", "You need to login at /login with username and password");
        return hashMap;
    }
    
    @PostMapping(path="/loginError")
    public @ResponseBody HashMap<String, String> showErrors(){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Message", "Login username / password error");
        return hashMap;
    }
    
    @PostMapping(path="/logout")
    public @ResponseBody HashMap<String, String> logout(){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Message", "You logout successfull");
        return hashMap;
    }
}
