package auca.ac.rw.service;

import auca.ac.rw.dao.BookDao;
import auca.ac.rw.dao.RoomDao;
import auca.ac.rw.dao.ShelfDao;
import auca.ac.rw.domain.Book;
import auca.ac.rw.domain.Room;
import auca.ac.rw.domain.Shelf;
import auca.ac.rw.exception.LocationNotFoundException;

import java.util.UUID;

/**
 * Requirement 8 & 9: physically placing books on shelves, and shelves in rooms.
 */
public class ShelfService {

    private final BookDao bookDao;
    private final ShelfDao shelfDao;
    private final RoomDao roomDao;

    public ShelfService() {
        this(new BookDao(), new ShelfDao(), new RoomDao());
    }

    public ShelfService(BookDao bookDao, ShelfDao shelfDao, RoomDao roomDao) {
        this.bookDao = bookDao;
        this.shelfDao = shelfDao;
        this.roomDao = roomDao;
    }

    /**
     * Requirement 8: assigns a book to a shelf. Updates the book's shelfId
     * and increments the shelf's available stock count.
     */
    public void assignBookToShelf(UUID bookId, UUID shelfId) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new LocationNotFoundException("Book not found for id: " + bookId));
        Shelf shelf = shelfDao.findById(shelfId)
                .orElseThrow(() -> new LocationNotFoundException("Shelf not found for id: " + shelfId));

        book.setShelfId(shelfId);
        bookDao.update(book);

        shelf.setAvailableStock(shelf.getAvailableStock() + 1);
        shelf.setInitialStock(shelf.getInitialStock() + 1);
        shelfDao.update(shelf);
    }

    /**
     * Requirement 9: assigns a shelf to a room.
     */
    public void assignShelfToRoom(UUID shelfId, UUID roomId) {
        Shelf shelf = shelfDao.findById(shelfId)
                .orElseThrow(() -> new LocationNotFoundException("Shelf not found for id: " + shelfId));
        Room room = roomDao.findById(roomId)
                .orElseThrow(() -> new LocationNotFoundException("Room not found for id: " + roomId));

        shelf.setRoomId(room.getRoomId());
        shelfDao.update(shelf);
    }
}
