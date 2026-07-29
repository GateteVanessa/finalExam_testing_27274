package auca.ac.rw.dao;

import auca.ac.rw.domain.Shelf;
import auca.ac.rw.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class ShelfDao extends AbstractDao<Shelf> {

    public ShelfDao() {
        super(Shelf.class);
    }

    public List<Shelf> findByRoomId(UUID roomId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Shelf where roomId = :roomId", Shelf.class)
                    .setParameter("roomId", roomId)
                    .list();
        }
    }
}
