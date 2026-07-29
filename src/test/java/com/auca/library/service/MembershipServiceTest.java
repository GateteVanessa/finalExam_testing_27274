package com.auca.library.service;

import com.auca.library.dao.MembershipDao;
import com.auca.library.dao.MembershipTypeDao;
import com.auca.library.dao.UserDao;
import com.auca.library.domain.Membership;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.User;
import com.auca.library.domain.enums.Gender;
import com.auca.library.domain.enums.MembershipStatus;
import com.auca.library.domain.enums.Role;
import com.auca.library.exception.MembershipAlreadyActiveException;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MembershipServiceTest extends BaseServiceTest {

    private MembershipService membershipService;
    private MembershipDao membershipDao;
    private MembershipTypeDao membershipTypeDao;
    private UserDao userDao;

    private MembershipType goldType;
    private User reader;

    @Before
    public void setUp() {
        membershipDao = new MembershipDao();
        membershipTypeDao = new MembershipTypeDao();
        userDao = new UserDao();
        membershipService = new MembershipService(membershipDao, membershipTypeDao);

        goldType = membershipTypeDao.save(new MembershipType("Gold", 5, 50));

        reader = userDao.save(new User("Alice", "Mukamana", Gender.FEMALE, "0788222222",
                "alice.mukamana", AuthService.hashPassword("Passw0rd!"), Role.STUDENT, null));
    }

    @Test
    public void registerMembership_gold_createsPendingMembershipLinkedToGoldType() {
        Membership membership = membershipService.registerMembership(
                reader.getPersonId(), goldType.getMembershipTypeId());

        assertEquals(MembershipStatus.PENDING, membership.getMembershipStatus());
        assertEquals(goldType.getMembershipTypeId(), membership.getMembershipTypeId());
        assertEquals(reader.getPersonId(), membership.getReaderId());
    }

    @Test(expected = MembershipAlreadyActiveException.class)
    public void registerMembership_userAlreadyHasActiveMembership_throwsException() {
        membershipService.registerMembership(reader.getPersonId(), goldType.getMembershipTypeId());
        // Second registration while the first is still PENDING/APPROVED must be rejected
        membershipService.registerMembership(reader.getPersonId(), goldType.getMembershipTypeId());
    }
}
