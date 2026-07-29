package auca.ac.rw.service;

import auca.ac.rw.dao.MembershipDao;
import auca.ac.rw.dao.MembershipTypeDao;
import auca.ac.rw.domain.Membership;
import auca.ac.rw.domain.MembershipType;
import auca.ac.rw.domain.enums.MembershipStatus;
import auca.ac.rw.exception.MembershipAlreadyActiveException;
import auca.ac.rw.exception.PersonNotFoundException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Requirement 5: a reader registers for a membership tier (Gold/Silver/Striver).
 */
public class MembershipService {

    private final MembershipDao membershipDao;
    private final MembershipTypeDao membershipTypeDao;

    public MembershipService() {
        this(new MembershipDao(), new MembershipTypeDao());
    }

    public MembershipService(MembershipDao membershipDao, MembershipTypeDao membershipTypeDao) {
        this.membershipDao = membershipDao;
        this.membershipTypeDao = membershipTypeDao;
    }

    /**
     * Creates a new membership in PENDING status. A user may not register a
     * second time while they already have an active (PENDING or APPROVED)
     * membership.
     */
    public Membership registerMembership(UUID userId, UUID membershipTypeId) {
        Optional<Membership> existing = membershipDao.findActiveByReaderId(userId);
        if (existing.isPresent()) {
            throw new MembershipAlreadyActiveException(
                    "User " + userId + " already has an active or pending membership");
        }

        MembershipType type = membershipTypeDao.findById(membershipTypeId)
                .orElseThrow(() -> new PersonNotFoundException(
                        "Membership type not found for id: " + membershipTypeId));

        Membership membership = new Membership(
                generateMembershipCode(type),
                userId,
                type.getMembershipTypeId(),
                MembershipStatus.PENDING,
                LocalDate.now(),
                LocalDate.now().plusYears(1)
        );

        return membershipDao.save(membership);
    }

    private String generateMembershipCode(MembershipType type) {
        String prefix = type.getMembershipName() == null
                ? "MEM"
                : type.getMembershipName().substring(0, Math.min(3, type.getMembershipName().length())).toUpperCase();
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
