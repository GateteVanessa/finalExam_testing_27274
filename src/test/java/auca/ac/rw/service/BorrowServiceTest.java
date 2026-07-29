package auca.ac.rw.service;

import auca.ac.rw.dao.*;
import auca.ac.rw.domain.*;
import auca.ac.rw.domain.enums.BookStatus;
import auca.ac.rw.domain.enums.Gender;
import auca.ac.rw.domain.enums.MembershipStatus;
import auca.ac.rw.domain.enums.Role;
import auca.ac.rw.exception.BorrowLimitExceededException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.*;

public class BorrowServiceTest extends BaseServiceTest {

    private BorrowService borrowService;
    private BorrowerDao borrowerDao;
    private BookDao bookDao;
    private MembershipDao membershipDao;
    private MembershipTypeDao membershipTypeDao;
    private UserDao userDao;

    private MembershipType goldType;
    private MembershipType silverType;
    private MembershipType striverType;

    @Before
    public void setUp() {
        borrowerDao = new BorrowerDao();
        bookDao = new BookDao();
        membershipDao = new MembershipDao();
        membershipTypeDao = new MembershipTypeDao();
        userDao = new UserDao();
        borrowService = new BorrowService(borrowerDao, bookDao, membershipDao, membershipTypeDao);

        goldType = membershipTypeDao.save(new MembershipType("Gold", 5, 50));
        silverType = membershipTypeDao.save(new MembershipType("Silver", 3, 30));
        striverType = membershipTypeDao.save(new MembershipType("Striver", 2, 10));
    }

    private User newReader(String username) {
        return userDao.save(new User("Test", username, Gender.MALE, "0788000000",
                username, AuthService.hashPassword("Passw0rd!"), Role.STUDENT, null));
    }

    private void approveMembership(UUID readerId, MembershipType type) {
        Membership membership = new Membership("MEM-" + UUID.randomUUID().toString().substring(0, 8),
                readerId, type.getMembershipTypeId(), MembershipStatus.APPROVED,
                LocalDate.now(), LocalDate.now().plusYears(1));
        membershipDao.save(membership);
    }

    private Book newAvailableBook(String title) {
        return bookDao.save(new Book(title, "1st", UUID.randomUUID().toString(), LocalDate.now(), "AUCA Press"));
    }

    private void createNActiveBorrows(UUID readerId, int n) {
        for (int i = 0; i < n; i++) {
            Book book = newAvailableBook("Filler book " + i);
            Borrower borrower = new Borrower(readerId, book.getBookId(), LocalDate.now(), LocalDate.now().plusDays(14));
            borrowerDao.save(borrower);
        }
    }

    // ---------- Requirement 6: borrowBook ----------

    @Test
    public void borrowBook_availableBook_createsBorrowerRecordWithZeroFine() {
        User reader = newReader("reader.zero.fine");
        approveMembership(reader.getPersonId(), goldType);
        Book book = newAvailableBook("Clean Code");

        Borrower borrower = borrowService.borrowBook(reader.getPersonId(), book.getBookId());

        assertNotNull(borrower.getId());
        assertEquals(0, borrower.getFine());
    }

    @Test
    public void borrowBook_setsBookStatusToBorrowed() {
        User reader = newReader("reader.status");
        approveMembership(reader.getPersonId(), goldType);
        Book book = newAvailableBook("Effective Java");

        borrowService.borrowBook(reader.getPersonId(), book.getBookId());

        Book refreshed = bookDao.findById(book.getBookId()).orElseThrow();
        assertEquals(BookStatus.BORROWED, refreshed.getBookStatus());
    }

    @Test
    public void borrowBook_dueDateIsPickupDatePlusLoanPeriod() {
        User reader = newReader("reader.duedate");
        approveMembership(reader.getPersonId(), goldType);
        Book book = newAvailableBook("Design Patterns");

        Borrower borrower = borrowService.borrowBook(reader.getPersonId(), book.getBookId());

        assertEquals(borrower.getPickupDate().plusDays(BorrowService.LOAN_PERIOD_DAYS), borrower.getDueDate());
    }

    // ---------- Requirement 7: validateBorrowLimit ----------

    @Test
    public void goldMember_withFourActiveBorrows_canBorrowAFifth() {
        User reader = newReader("gold.four");
        approveMembership(reader.getPersonId(), goldType);
        createNActiveBorrows(reader.getPersonId(), 4);

        // should not throw
        borrowService.validateBorrowLimit(reader.getPersonId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void goldMember_withFiveActiveBorrows_cannotBorrowASixth() {
        User reader = newReader("gold.five");
        approveMembership(reader.getPersonId(), goldType);
        createNActiveBorrows(reader.getPersonId(), 5);

        borrowService.validateBorrowLimit(reader.getPersonId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void silverMember_withThreeActiveBorrows_isBlocked() {
        User reader = newReader("silver.three");
        approveMembership(reader.getPersonId(), silverType);
        createNActiveBorrows(reader.getPersonId(), 3);

        borrowService.validateBorrowLimit(reader.getPersonId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void striverMember_withTwoActiveBorrows_isBlocked() {
        User reader = newReader("striver.two");
        approveMembership(reader.getPersonId(), striverType);
        createNActiveBorrows(reader.getPersonId(), 2);

        borrowService.validateBorrowLimit(reader.getPersonId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void userWithoutApprovedMembership_isBlocked() {
        User reader = newReader("no.membership");

        borrowService.validateBorrowLimit(reader.getPersonId());
    }

    // ---------- Requirement 12: calculateLateFee ----------

    @Test
    public void returnedOnDueDate_feeIsZero() {
        User reader = newReader("fee.ontime");
        approveMembership(reader.getPersonId(), goldType);
        Book book = newAvailableBook("On Time Book");
        Borrower borrower = new Borrower(reader.getPersonId(), book.getBookId(),
                LocalDate.now().minusDays(14), LocalDate.now());
        borrower.setReturnDate(LocalDate.now());
        borrower = borrowerDao.save(borrower);

        assertEquals(0, borrowService.calculateLateFee(borrower.getId()));
    }

    @Test
    public void goldMember_returnedThreeDaysLate_feeIs150() {
        User reader = newReader("fee.gold.3");
        approveMembership(reader.getPersonId(), goldType);
        Book book = newAvailableBook("Gold Late Book");
        Borrower borrower = new Borrower(reader.getPersonId(), book.getBookId(),
                LocalDate.now().minusDays(17), LocalDate.now().minusDays(3));
        borrower.setReturnDate(LocalDate.now());
        borrower = borrowerDao.save(borrower);

        assertEquals(150, borrowService.calculateLateFee(borrower.getId()));
    }

    @Test
    public void silverMember_returnedFiveDaysLate_feeIs150() {
        User reader = newReader("fee.silver.5");
        approveMembership(reader.getPersonId(), silverType);
        Book book = newAvailableBook("Silver Late Book");
        Borrower borrower = new Borrower(reader.getPersonId(), book.getBookId(),
                LocalDate.now().minusDays(19), LocalDate.now().minusDays(5));
        borrower.setReturnDate(LocalDate.now());
        borrower = borrowerDao.save(borrower);

        assertEquals(150, borrowService.calculateLateFee(borrower.getId()));
    }

    @Test
    public void striverMember_returnedOneDayLate_feeIs10() {
        User reader = newReader("fee.striver.1");
        approveMembership(reader.getPersonId(), striverType);
        Book book = newAvailableBook("Striver Late Book");
        Borrower borrower = new Borrower(reader.getPersonId(), book.getBookId(),
                LocalDate.now().minusDays(15), LocalDate.now().minusDays(1));
        borrower.setReturnDate(LocalDate.now());
        borrower = borrowerDao.save(borrower);

        assertEquals(10, borrowService.calculateLateFee(borrower.getId()));
    }

    @Test
    public void notYetReturned_feeIsComputedAgainstToday() {
        User reader = newReader("fee.notreturned");
        approveMembership(reader.getPersonId(), goldType);
        Book book = newAvailableBook("Still Out Book");
        Borrower borrower = new Borrower(reader.getPersonId(), book.getBookId(),
                LocalDate.now().minusDays(16), LocalDate.now().minusDays(2));
        // returnDate intentionally left null
        borrower = borrowerDao.save(borrower);

        assertEquals(100, borrowService.calculateLateFee(borrower.getId())); // 2 days x 50 Rwf
    }
}
