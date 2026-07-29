package com.auca.library.service;

import com.auca.library.dao.MembershipDao;
import com.auca.library.dao.MembershipTypeDao;
import com.auca.library.domain.Membership;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.enums.MembershipStatus;
import com.auca.library.exception.MembershipAlreadyActiveException;
import com.auca.library.exception.PersonNotFoundException;

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
