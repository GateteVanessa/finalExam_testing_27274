package auca.ac.rw.service;

import auca.ac.rw.dao.BookDao;
import auca.ac.rw.dao.BorrowerDao;
import auca.ac.rw.dao.MembershipDao;
import auca.ac.rw.dao.MembershipTypeDao;
import auca.ac.rw.domain.Book;
import auca.ac.rw.domain.Borrower;
import auca.ac.rw.domain.Membership;
import auca.ac.rw.domain.MembershipType;
import auca.ac.rw.domain.enums.BookStatus;
import auca.ac.rw.exception.BookNotAvailableException;
import auca.ac.rw.exception.BorrowLimitExceededException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Requirement 6, 7 & 12: borrowing books, enforcing the membership borrow
 * limit, and computing late fees.
 */
public class BorrowService {

    /** Fixed loan period used to compute the due date from the pickup date. */
    public static final int LOAN_PERIOD_DAYS = 14;

    private final BorrowerDao borrowerDao;
    private final BookDao bookDao;
    private final MembershipDao membershipDao;
    private final MembershipTypeDao membershipTypeDao;

    public BorrowService() {
        this(new BorrowerDao(), new BookDao(), new MembershipDao(), new MembershipTypeDao());
    }

    public BorrowService(BorrowerDao borrowerDao, BookDao bookDao,
                          MembershipDao membershipDao, MembershipTypeDao membershipTypeDao) {
        this.borrowerDao = borrowerDao;
        this.bookDao = bookDao;
        this.membershipDao = membershipDao;
        this.membershipTypeDao = membershipTypeDao;
    }

    /**
     * Requirement 7: throws BorrowLimitExceededException if the reader has
     * no approved membership, or already has as many active borrows as their
     * membership tier allows.
     */
    public void validateBorrowLimit(UUID readerId) {
        Membership membership = membershipDao.findApprovedByReaderId(readerId)
                .orElseThrow(() -> new BorrowLimitExceededException(
                        "Reader " + readerId + " has no approved membership"));

        MembershipType type = membershipTypeDao.findById(membership.getMembershipTypeId())
                .orElseThrow(() -> new BorrowLimitExceededException(
                        "Membership type not found for membership " + membership.getMembershipId()));

        int activeBorrows = borrowerDao.findActiveByReaderId(readerId).size();
        if (activeBorrows >= type.getMaxBooks()) {
            throw new BorrowLimitExceededException(
                    "Reader " + readerId + " has reached their borrow limit of " + type.getMaxBooks());
        }
    }

    /**
     * Requirement 6: creates a Borrower record for an available book. The
     * fine starts at zero, the due date is pickup date + loan period, and
     * the book's status flips to BORROWED.
     */
    public Borrower borrowBook(UUID readerId, UUID bookId) {
        validateBorrowLimit(readerId);

        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new BookNotAvailableException("Book not found for id: " + bookId));

        if (book.getBookStatus() != BookStatus.AVAILABLE) {
            throw new BookNotAvailableException("Book " + bookId + " is not available for borrowing");
        }

        LocalDate pickupDate = LocalDate.now();
        LocalDate dueDate = pickupDate.plusDays(LOAN_PERIOD_DAYS);

        Borrower borrower = new Borrower(readerId, bookId, pickupDate, dueDate);
        borrower.setFine(0);
        Borrower saved = borrowerDao.save(borrower);

        book.setBookStatus(BookStatus.BORROWED);
        bookDao.update(book);

        return saved;
    }

    /**
     * Requirement 12: daysLate x the reader's membership daily rate.
     * Uses the return date if the book has been returned, otherwise compares
     * the due date against today (still-outstanding loans keep accruing).
     */
    public int calculateLateFee(UUID borrowerId) {
        Borrower borrower = borrowerDao.findById(borrowerId)
                .orElseThrow(() -> new IllegalArgumentException("Borrower record not found for id: " + borrowerId));

        LocalDate effectiveDate = borrower.getReturnDate() != null ? borrower.getReturnDate() : LocalDate.now();

        long daysLate = ChronoUnit.DAYS.between(borrower.getDueDate(), effectiveDate);
        if (daysLate <= 0) {
            return 0;
        }

        Membership membership = membershipDao.findApprovedByReaderId(borrower.getReaderId())
                .orElseThrow(() -> new IllegalStateException(
                        "No approved membership found for reader " + borrower.getReaderId()));
        MembershipType type = membershipTypeDao.findById(membership.getMembershipTypeId())
                .orElseThrow(() -> new IllegalStateException(
                        "Membership type not found for membership " + membership.getMembershipId()));

        return (int) daysLate * type.getPrice();
    }

    List<Borrower> activeBorrowsFor(UUID readerId) {
        return borrowerDao.findActiveByReaderId(readerId);
    }
}
