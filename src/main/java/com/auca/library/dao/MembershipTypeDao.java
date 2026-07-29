package com.auca.library.dao;

import com.auca.library.domain.MembershipType;
import com.auca.library.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Optional;

public class MembershipTypeDao extends AbstractDao<MembershipType> {

    public MembershipTypeDao() {
        super(MembershipType.class);
    }

    public Optional<MembershipType> findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            MembershipType type = session.createQuery(
                            "from MembershipType where membershipName = :name", MembershipType.class)
                    .setParameter("name", name)
                    .uniqueResult();
            return Optional.ofNullable(type);
        }
    }
}
