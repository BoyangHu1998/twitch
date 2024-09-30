package com.boyangh.twitch.dao;

import com.boyangh.twitch.entity.db.User;
import org.hibernate.Session;  // Hibernate Session for database operations
import org.hibernate.SessionFactory;  // Hibernate SessionFactory for managing sessions
import org.springframework.beans.factory.annotation.Autowired;  // Autowired for dependency injection
import org.springframework.stereotype.Repository;  // Repository annotation for DAO classes

@Repository  // Marks this class as a Spring repository
public class LoginDao {

    @Autowired
    private SessionFactory sessionFactory;  // SessionFactory instance injected by Spring

    // Verifies the userId and password. Returns the username if credentials are correct.
    public String verifyLogin(String userId, String password) {
        String name = "";  // Initialize the name to an empty string

        try (Session session = sessionFactory.openSession()) {  // Open a Hibernate session
            User user = session.get(User.class, userId);  // Retrieve the User object using the userId
            if (user != null && user.getPassword().equals(password)) {  // Check if the user exists and password matches
                name = user.getFirstName();  // If valid, set name to user's first name
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return name;  // Return the user's first name if login is successful, else return empty string
    }
}