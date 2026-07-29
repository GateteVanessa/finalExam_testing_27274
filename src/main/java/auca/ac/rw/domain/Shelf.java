package auca.ac.rw.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "shelves")
public class Shelf {

    @Id
    @GeneratedValue
    @Column(name = "shelf_id", updatable = false, nullable = false)
    private UUID shelfId;

    @Column(name = "book_category")
    private String bookCategory;

    @Column(name = "borrowed_number", nullable = false)
    private int borrowedNumber = 0;

    @Column(name = "initial_stock", nullable = false)
    private int initialStock = 0;

    @Column(name = "available_stock", nullable = false)
    private int availableStock = 0;

    /**
     * Which room this shelf currently sits in. Null until assigned via
     * assignShelfToRoom().
     */
    @Column(name = "room_id")
    private UUID roomId;

    public Shelf() {
    }

    public Shelf(String bookCategory, int initialStock) {
        this.bookCategory = bookCategory;
        this.initialStock = initialStock;
        this.availableStock = initialStock;
        this.borrowedNumber = 0;
    }

    public UUID getShelfId() {
        return shelfId;
    }

    public void setShelfId(UUID shelfId) {
        this.shelfId = shelfId;
    }

    public String getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(String bookCategory) {
        this.bookCategory = bookCategory;
    }

    public int getBorrowedNumber() {
        return borrowedNumber;
    }

    public void setBorrowedNumber(int borrowedNumber) {
        this.borrowedNumber = borrowedNumber;
    }

    public int getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(int initialStock) {
        this.initialStock = initialStock;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }
}
