package auca.ac.rw.domain;

import auca.ac.rw.domain.enums.MembershipStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * A reader's registration for a given {@link MembershipType}. Starts life
 * PENDING and must be APPROVED (e.g. by the librarian) before the reader is
 * allowed to borrow books.
 */
@Entity
@Table(name = "memberships")
public class Membership {

    @Id
    @GeneratedValue
    @Column(name = "membership_id", updatable = false, nullable = false)
    private UUID membershipId;

    @Column(name = "membership_code", nullable = false, unique = true)
    private String membershipCode;

    @Column(name = "reader_id", nullable = false)
    private UUID readerId;

    @Column(name = "membership_type_id", nullable = false)
    private UUID membershipTypeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_status", nullable = false)
    private MembershipStatus membershipStatus;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "expiring_time")
    private LocalDate expiringTime;

    public Membership() {
    }

    public Membership(String membershipCode, UUID readerId, UUID membershipTypeId,
                       MembershipStatus membershipStatus, LocalDate registrationDate, LocalDate expiringTime) {
        this.membershipCode = membershipCode;
        this.readerId = readerId;
        this.membershipTypeId = membershipTypeId;
        this.membershipStatus = membershipStatus;
        this.registrationDate = registrationDate;
        this.expiringTime = expiringTime;
    }

    public UUID getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(UUID membershipId) {
        this.membershipId = membershipId;
    }

    public String getMembershipCode() {
        return membershipCode;
    }

    public void setMembershipCode(String membershipCode) {
        this.membershipCode = membershipCode;
    }

    public UUID getReaderId() {
        return readerId;
    }

    public void setReaderId(UUID readerId) {
        this.readerId = readerId;
    }

    public UUID getMembershipTypeId() {
        return membershipTypeId;
    }

    public void setMembershipTypeId(UUID membershipTypeId) {
        this.membershipTypeId = membershipTypeId;
    }

    public MembershipStatus getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(MembershipStatus membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDate getExpiringTime() {
        return expiringTime;
    }

    public void setExpiringTime(LocalDate expiringTime) {
        this.expiringTime = expiringTime;
    }
}
