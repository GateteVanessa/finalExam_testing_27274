package com.auca.library.service;

import com.auca.library.dao.RoomDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RoomServiceTest extends BaseServiceTest {

    private RoomService roomService;
    private RoomDao roomDao;
    private ShelfDao shelfDao;

    @Before
    public void setUp() {
        roomDao = new RoomDao();
        shelfDao = new ShelfDao();
        roomService = new RoomService(roomDao, shelfDao);
    }

    // ---------- Requirement 10: countBooksInRoom ----------

    @Test
    public void roomWithMultipleShelves_sumsBookCountsAcrossShelves() {
        Room room = roomDao.save(new Room("R-201"));

        Shelf shelfA = new Shelf("Fiction", 10);
        shelfA.setRoomId(room.getRoomId());
        shelfDao.save(shelfA);

        Shelf shelfB = new Shelf("Non-Fiction", 7);
        shelfB.setRoomId(room.getRoomId());
        shelfDao.save(shelfB);

        int total = roomService.countBooksInRoom(room.getRoomId());

        assertEquals(17, total);
    }

    @Test
    public void roomWithNoShelves_returnsZero() {
        Room room = roomDao.save(new Room("R-202"));

        int total = roomService.countBooksInRoom(room.getRoomId());

        assertEquals(0, total);
    }

    // ---------- Requirement 11: findRoomWithFewestBooks ----------

    @Test
    public void multipleRooms_returnsRoomWithLowestBookCount() {
        Room busyRoom = roomDao.save(new Room("R-301"));
        Shelf busyShelf = new Shelf("Fiction", 20);
        busyShelf.setRoomId(busyRoom.getRoomId());
        shelfDao.save(busyShelf);

        Room quietRoom = roomDao.save(new Room("R-302"));
        Shelf quietShelf = new Shelf("Reference", 2);
        quietShelf.setRoomId(quietRoom.getRoomId());
        shelfDao.save(quietShelf);

        Room result = roomService.findRoomWithFewestBooks();

        assertEquals(quietRoom.getRoomId(), result.getRoomId());
    }
}
