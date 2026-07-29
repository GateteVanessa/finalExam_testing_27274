package auca.ac.rw.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * One borrow transaction: a reader picking up one physical book.
 * fine starts at 0 on the pickup date and is (re)computed by
 * BorrowService.calculateLateFee().
 */
@Entity
@Table(name = "borrowers")
public class Borrower {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "reader_id", nullable = false)
    private UUID readerId;

    @Column(name = "book_id", nullable = false)
    private UUID bookId;

    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /**
     * Actually null while the book is still out; set when the reader returns it.
     */
    @Column(name = "return_date")
    private LocalDate returnDate;

    /**
     * Late charge fee in Rwf. Initialized to zero at borrow time.
     */
    @Column(name = "late_charge_fees", nullable = false)
    private int fine = 0;

    public Borrower() {
    }

    public Borrower(UUID readerId, UUID bookId, LocalDate pickupDate, LocalDate dueDate) {
        this.readerId = readerId;
        this.bookId = bookId;
        this.pickupDate = pickupDate;
        this.dueDate = dueDate;
        this.fine = 0;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReaderId() {
        return readerId;
    }

    public void setReaderId(UUID readerId) {
        this.readerId = readerId;
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }
}
