package com.boyangh.twitch.service;

import com.boyangh.twitch.dao.LoginDao;
import com.boyangh.twitch.util.Util;
import org.springframework.beans.factory.annotation.Autowired;  // Annotation for dependency injection
import org.springframework.stereotype.Service;  // Annotation to declare this as a service class
import java.io.IOException;  // Exception handling

@Service  // Marks this class as a service managed by Spring
public class LoginService {

    @Autowired  // Dependency injection for LoginDao
    private LoginDao loginDao;

    // Method to verify login credentials. It calls the verifyLogin method in LoginDao.
    public String verifyLogin(String userId, String password) throws IOException {
        password = Util.encryptPassword(userId, password);
        return loginDao.verifyLogin(userId, password);  // Delegating the call to the DAO layer
    }
}