package auca.ac.rw.domain;

import auca.ac.rw.domain.enums.BookStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue
    @Column(name = "book_id", updatable = false, nullable = false)
    private UUID bookId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "edition")
    private String edition;

    @Column(name = "isbn_code", unique = true)
    private String isbnCode;

    @Column(name = "publication_year")
    private LocalDate publicationYear;

    @Column(name = "publisher_name")
    private String publisherName;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_status", nullable = false)
    private BookStatus bookStatus = BookStatus.AVAILABLE;

    /**
     * Which shelf currently holds this book. Null until assignBookToShelf().
     */
    @Column(name = "shelf_id")
    private UUID shelfId;

    public Book() {
    }

    public Book(String title, String edition, String isbnCode, LocalDate publicationYear, String publisherName) {
        this.title = title;
        this.edition = edition;
        this.isbnCode = isbnCode;
        this.publicationYear = publicationYear;
        this.publisherName = publisherName;
        this.bookStatus = BookStatus.AVAILABLE;
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getIsbnCode() {
        return isbnCode;
    }

    public void setIsbnCode(String isbnCode) {
        this.isbnCode = isbnCode;
    }

    public LocalDate getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(LocalDate publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public BookStatus getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(BookStatus bookStatus) {
        this.bookStatus = bookStatus;
    }

    public UUID getShelfId() {
        return shelfId;
    }

    public void setShelfId(UUID shelfId) {
        this.shelfId = shelfId;
    }
}
