package com.boyangh.twitch.dao;


import com.boyangh.twitch.entity.db.User;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RegisterDao {

    @Autowired
    private SessionFactory sessionFactory;

    public boolean register(User user) {
        Session session = null;

        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.persist(user);  // Save the user to the database.
            session.getTransaction().commit();  // Commit the transaction.
        } catch (PersistenceException | IllegalStateException ex) {
            // If Hibernate throws an exception, it indicates the user might already be registered.
            ex.printStackTrace();
            session.getTransaction().rollback();  // Roll back the transaction in case of an error.
            return false;
        } finally {
            if (session != null) session.close();  // Close the session to release resources.
        }
        return true;  // Return true if registration was successful.
    }
}
