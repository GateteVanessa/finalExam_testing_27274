package auca.ac.rw.service;

import auca.ac.rw.dao.BookDao;
import auca.ac.rw.dao.RoomDao;
import auca.ac.rw.dao.ShelfDao;
import auca.ac.rw.domain.Book;
import auca.ac.rw.domain.Room;
import auca.ac.rw.domain.Shelf;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ShelfServiceTest extends BaseServiceTest {

    private ShelfService shelfService;
    private BookDao bookDao;
    private ShelfDao shelfDao;
    private RoomDao roomDao;

    @Before
    public void setUp() {
        bookDao = new BookDao();
        shelfDao = new ShelfDao();
        roomDao = new RoomDao();
        shelfService = new ShelfService(bookDao, shelfDao, roomDao);
    }

    // ---------- Requirement 8: assignBookToShelf ----------

    @Test
    public void assignBookToShelf_updatesBookShelfId() {
        Book book = bookDao.save(new Book("The Pragmatic Programmer", "2nd",
                UUID.randomUUID().toString(), LocalDate.now(), "Addison-Wesley"));
        Shelf shelf = shelfDao.save(new Shelf("Software Engineering", 0));

        shelfService.assignBookToShelf(book.getBookId(), shelf.getShelfId());

        Book refreshed = bookDao.findById(book.getBookId()).orElseThrow();
        assertEquals(shelf.getShelfId(), refreshed.getShelfId());
    }

    @Test
    public void assignBookToShelf_incrementsShelfAvailableStock() {
        Book book = bookDao.save(new Book("Refactoring", "2nd",
                UUID.randomUUID().toString(), LocalDate.now(), "Addison-Wesley"));
        Shelf shelf = shelfDao.save(new Shelf("Software Engineering", 3));

        shelfService.assignBookToShelf(book.getBookId(), shelf.getShelfId());

        Shelf refreshed = shelfDao.findById(shelf.getShelfId()).orElseThrow();
        assertEquals(4, refreshed.getAvailableStock());
    }

    // ---------- Requirement 9: assignShelfToRoom ----------

    @Test
    public void assignShelfToRoom_updatesShelfRoomId() {
        Shelf shelf = shelfDao.save(new Shelf("History", 0));
        Room room = roomDao.save(new Room("R-101"));

        shelfService.assignShelfToRoom(shelf.getShelfId(), room.getRoomId());

        Shelf refreshed = shelfDao.findById(shelf.getShelfId()).orElseThrow();
        assertEquals(room.getRoomId(), refreshed.getRoomId());
    }
}
