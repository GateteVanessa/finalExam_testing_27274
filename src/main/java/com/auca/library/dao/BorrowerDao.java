package com.auca.library.dao;

import com.auca.library.domain.Borrower;
import com.auca.library.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class BorrowerDao extends AbstractDao<Borrower> {

    public BorrowerDao() {
        super(Borrower.class);
    }

    /**
     * Books currently checked out (not yet returned) by this reader.
     */
    public List<Borrower> findActiveByReaderId(UUID readerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Borrower where readerId = :readerId and returnDate is null", Borrower.class)
                    .setParameter("readerId", readerId)
                    .list();
        }
    }

    /**
     * Latest (open or most recent) borrow record for a given book, if any.
     */
    public List<Borrower> findByBookId(UUID bookId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Borrower where bookId = :bookId order by pickupDate desc", Borrower.class)
                    .setParameter("bookId", bookId)
                    .list();
        }
    }
}
