package com.auca.library.dao;

import com.auca.library.domain.User;
import com.auca.library.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Optional;

public class UserDao extends AbstractDao<User> {

    public UserDao() {
        super(User.class);
    }

    public Optional<User> findByUsername(String userName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.createQuery(
                            "from User where userName = :userName", User.class)
                    .setParameter("userName", userName)
                    .uniqueResult();
            return Optional.ofNullable(user);
        }
    }
}
