package com.boyangh.twitch.dao;

import com.boyangh.twitch.entity.db.Item;
import com.boyangh.twitch.entity.db.ItemType;
import com.boyangh.twitch.entity.db.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FavoriteDao {
    @Autowired
    private SessionFactory sessionFactory;

    // Insert a favorite record to the database
    public void setFavoriteItem(String userId, Item item) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            User user = session.get(User.class, userId);
            Set<Item> userItemSet = user.getItemSet();
            userItemSet.add(item);
            session.beginTransaction();
            session.merge(user);  // Use merge to update existing user and its associated items
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            assert session != null;
            session.getTransaction().rollback();
        } finally {
            if (session != null) session.close();
        }
    }

    // Remove a favorite record from the database
    public void unsetFavoriteItem(String userId, String itemId) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            User user = session.get(User.class, userId);
            Item item = session.get(Item.class, itemId);
            user.getItemSet().remove(item);

            session.beginTransaction();
            session.merge(user);  // instead save : ref: https://www.baeldung.com/hibernate-save-persist-update-merge-saveorupdate
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            if (session != null) session.close();
        }
    }

    // Retrieve favorite items from the database
    public Set<Item> getFavoriteItems(String userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, userId).getItemSet();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new HashSet<>();
    }
}