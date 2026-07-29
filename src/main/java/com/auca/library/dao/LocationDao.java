package com.auca.library.dao;

import com.auca.library.domain.Location;
import com.auca.library.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Optional;
import java.util.UUID;

public class LocationDao extends AbstractDao<Location> {

    public LocationDao() {
        super(Location.class);
    }

    public Optional<Location> findByCode(String locationCode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Location location = session.createQuery(
                            "from Location where locationCode = :code", Location.class)
                    .setParameter("code", locationCode)
                    .uniqueResult();
            return Optional.ofNullable(location);
        }
    }
}
