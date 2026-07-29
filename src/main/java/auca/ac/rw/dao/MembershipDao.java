package auca.ac.rw.dao;

import auca.ac.rw.domain.Membership;
import auca.ac.rw.domain.enums.MembershipStatus;
import auca.ac.rw.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MembershipDao extends AbstractDao<Membership> {

    public MembershipDao() {
        super(Membership.class);
    }

    public List<Membership> findByReaderId(UUID readerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Membership where readerId = :readerId", Membership.class)
                    .setParameter("readerId", readerId)
                    .list();
        }
    }

    public Optional<Membership> findActiveByReaderId(UUID readerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Membership> results = session.createQuery(
                            "from Membership where readerId = :readerId and membershipStatus in (:statuses)",
                            Membership.class)
                    .setParameter("readerId", readerId)
                    .setParameter("statuses", List.of(MembershipStatus.APPROVED, MembershipStatus.PENDING))
                    .list();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }
    }

    public Optional<Membership> findApprovedByReaderId(UUID readerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Membership membership = session.createQuery(
                            "from Membership where readerId = :readerId and membershipStatus = :status",
                            Membership.class)
                    .setParameter("readerId", readerId)
                    .setParameter("status", MembershipStatus.APPROVED)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(membership);
        }
    }
}
